package top.potens.jnet.bootstrap;

import io.netty.channel.group.ChannelGroupFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.handler.*;
import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCReqHandlerListener;
import top.potens.jnet.protocol.HBinaryProtocol;
import top.potens.jnet.protocol.HBinaryProtocolDecoder;
import top.potens.jnet.protocol.HBinaryProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshao on 2018/5/6.
 * 负责心跳检查、普通消息传输
 * 端口31416
 */
public class BossServer {
    private static final Logger logger = LoggerFactory.getLogger(BossServer.class);

    // 监听端口
    private int port;
    // 文件接收保存的目录
    private String fileUpSaveDir;
    private FileHandler fileHandler;
    private FileCallback fileReceiveCallback;
    private BossServerEndHandler endHandler;
    private RPCReqHandlerListener rpcReqListener;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public BossServer() {
        initDefault();
    }

    private void initDefault() {
        this.port = 31416;
        this.fileUpSaveDir = "/d/tmp";
    }

    public int getPort() {
        return port;
    }

    public String getFileUpSaveDir() {
        return fileUpSaveDir;
    }


    // Fluent风格api=====================================

    /**
     * 设置监听的端口
     *
     * @param port 端口号
     * @return this
     */
    public BossServer listenerPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * 设置文件上传保存路径
     *
     * @param dir 目录
     * @return this
     */
    public BossServer fileUpSaveDir(String dir) {
        this.fileUpSaveDir = dir;
        return this;
    }

    /**
     * 设置接受文件回调
     *
     * @param fileCallback 回调
     * @return this
     */
    public BossServer receiveFile(FileCallback fileCallback) {
        this.fileReceiveCallback = fileCallback;
        return this;
    }

    public BossServer setRPCReqListener(RPCReqHandlerListener rpcReqListener) {
        this.rpcReqListener = rpcReqListener;
        return this;
    }


    // ==============================

    // 广播到所有的client
    public boolean broadcastEvent(String method, String string) {
        ChannelGroupFuture broadcast = ChannelGroupHelper.broadcast(HBinaryProtocol.buildEventAll(method, string));
        return true;
    }

    // 广播到组的所有client
    public boolean broadcastEvent(String method, String string, String groupId) {
        ChannelGroupFuture broadcast = ChannelGroupHelper.broadcast(HBinaryProtocol.buildEventAll(method, string), groupId);
        return true;
    }

    // 发送到指定的client
    public boolean assignEvent(String method, String string, String channelId) {
        return ChannelGroupHelper.sendAssign(HBinaryProtocol.buildEventAll(method, string), channelId);
    }
    public ChannelFuture start() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(this.port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        fileHandler = new FileHandler(fileReceiveCallback, fileUpSaveDir);
                        endHandler = new BossServerEndHandler(rpcReqListener);
                        pipeline.addLast("ping", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast("unpacking", new LengthFieldBasedFrameDecoder(HBinaryProtocol.MAX_LENGTH, 0, 4, 0, 4));
                        pipeline.addLast("decoder", new HBinaryProtocolDecoder());
                        pipeline.addLast("encoder", new HBinaryProtocolEncoder());
                        pipeline.addLast("heart", new HeartBeatServerHandler());
                        pipeline.addLast("forward", new ForwardHandler());
                        //pipeline.addLast("file",fileHandler);
                        // pipeline.addLast("rpc", new RPCHandler());
                        pipeline.addLast("end", endHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 100)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind();
    }
    // 释放资源
    public void release(){

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
