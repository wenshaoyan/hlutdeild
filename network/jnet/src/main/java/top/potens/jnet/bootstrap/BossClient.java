package top.potens.jnet.bootstrap;


import com.google.gson.Gson;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.event.EventSource;
import top.potens.jnet.handler.*;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCCallback;
import top.potens.jnet.protocol.HBinaryProtocol;
import top.potens.jnet.protocol.HBinaryProtocolDecoder;
import top.potens.jnet.protocol.HBinaryProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshao on 2018/5/6.
 * 主通信进程的client
 */
public class BossClient {
    private static final Logger logger = LoggerFactory.getLogger(BossClient.class);
    private Gson gson = new Gson();
    private int port;
    private String host;
    private FileHandler mFileHandler;
    private FileCallback fileReceiveCallback;
    private EventHandler mEventHandler;
    private EventSource eventSource;
    private NioEventLoopGroup workerGroup;
    private String fileUpSaveDir;
    private RPCHandler mRPCHandler;
    private BossClientEndHandler mEndHandler;
    // channel
    private Channel mChannel;
    public enum ConnectStatus {
        READY, CONNECTING, SUCCESS,CLOSE
    }
    private ConnectStatus connectStatus;

    public BossClient() {
        initDefault();
        this.eventSource = new EventSource();
    }

    private void initDefault() {
        this.fileUpSaveDir = "/d/tmp";
    }

    public String getFileUpSaveDir() {
        return fileUpSaveDir;
    }

    public ConnectStatus getConnectStatus() {
        return connectStatus;
    }

    // Fluent风格api=====================================

    /**
     * 设置server的地址
     *
     * @param host ip
     * @param port 端口
     * @return this
     */
    public BossClient connect(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    /**
     * 设置接受文件回调
     *
     * @param fileCallback 回调
     * @return this
     */
    public BossClient receiveFile(FileCallback fileCallback) {
        this.fileReceiveCallback = fileCallback;
        return this;
    }

    /**
     * 添加server event
     *
     * @param eventListener listener
     * @return this
     */
    public BossClient addServerEventListener(EventListener eventListener) {
        this.eventSource.addListener(eventListener);
        return this;
    }

    /**
     * 设置文件上传保存路径
     *
     * @param dir 目录
     * @return this
     */
    public BossClient fileUpSaveDir(String dir) {
        this.fileUpSaveDir = dir;
        return this;
    }
    // ===========

    /**
     * 发送文本
     *
     * @param str 对应的文本
     */
    public void sendText(String str) {
    }

    /**
     * 发送文本
     *
     * @param str 对应的文本
     */
    public void sendJson(String str) {
    }

    /**
     * 发送本地的文件
     *
     * @param file 文件对象
     */
    public void sendFile(File file, byte receive, String receiveId, FileCallback fileCallback) throws FileNotFoundException {
        int id = HBinaryProtocol.randomId();
        mFileHandler.sendFileApply(mChannel, file, id, receive, receiveId, fileCallback);
    }

    /**
     * 发送rpc请求
     *
     * @param rpcHeader   请求头
     * @param rpcCallback 响应回调
     */
    public void sendRPC(RPCHeader rpcHeader, RPCCallback rpcCallback) {
        if (rpcHeader.getMethod() != null) {
            String stringRPCHeader = gson.toJson(rpcHeader);
            HBinaryProtocol protocol = HBinaryProtocol.buildReqRPC(stringRPCHeader);
            mChannel.writeAndFlush(protocol);
            mRPCHandler.addReq(protocol.getId(), rpcCallback);
        } else {
            if (rpcCallback != null) {
                rpcCallback.error("rpcHeader.method is null");
            }
        }
    }

    public ChannelFuture start() {
        workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        connectStatus = ConnectStatus.READY;
        b.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("ping", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                        pipeline.addLast("unpacking", new LengthFieldBasedFrameDecoder(HBinaryProtocol.MAX_LENGTH, 0, 4, 0, 4));
                        pipeline.addLast("decoder", new HBinaryProtocolDecoder());
                        pipeline.addLast("encoder", new HBinaryProtocolEncoder());
                        pipeline.addLast("heart", new HeartBeatClientHandler());
                        pipeline.addLast("file", new FileHandler(fileReceiveCallback));
                        pipeline.addLast("rpc", new RPCHandler());
                        pipeline.addLast("event", new EventHandler(eventSource));
                        pipeline.addLast("end", new BossClientEndHandler());
                    }
                });
        ChannelFuture connect = b.connect(this.host, this.port);

        connectStatus = ConnectStatus.CONNECTING;
        connect.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {   // 连接成功
                    connectStatus = ConnectStatus.SUCCESS;
                    mChannel = future.channel();
                    ChannelPipeline pipeline = future.channel().pipeline();
                    mRPCHandler = (RPCHandler) pipeline.get("rpc");
                    mFileHandler = (FileHandler) pipeline.get("file");
                    mEventHandler = (EventHandler) pipeline.get("event");
                    mEndHandler = (BossClientEndHandler) pipeline.get("end");
                } else {    // 连接异常
                    connectStatus = ConnectStatus.CLOSE;
                }
            }
        });
        ChannelFuture channelFuture = connect.channel().closeFuture();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                if (connectStatus == ConnectStatus.CLOSE ){ // 连接不上server造成的异常
                    logger.error("连接异常, ip=" + host + ",port="+port);
                } else {    // 和service断开的异常
                    logger.error("连接断开, ip=" + host + ",port="+port);
                }

            }
        });
        return connect;
    }


    // 释放资源
    public void release() {
        workerGroup.shutdownGracefully();
    }
}
