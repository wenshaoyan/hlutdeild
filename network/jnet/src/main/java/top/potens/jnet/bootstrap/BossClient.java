package top.potens.jnet.bootstrap;

import top.potens.jnet.handler.BossClientHandler;
import top.potens.jnet.handler.FileHandler;
import top.potens.jnet.handler.HeartBeatClientHandler;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.protocol.HBinaryProtocol;
import top.potens.jnet.protocol.HBinaryProtocolDecoder;
import top.potens.jnet.protocol.HBinaryProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshao on 2018/5/6.
 * 主通信进程的client
 */
public class BossClient {
    private int port;
    private String host;
    private FileHandler fileHandler;

    public BossClient() {}
    // Fluent风格api=====================================

    /**
     * 设置server的地址
     * @param host      ip
     * @param port      端口
     * @return          this
     */
    public BossClient connect(String host, int port){
        this.host = host;
        this.port = port;
        return this;
    }
    // ===========
    /**
     * 发送文本
     * @param str               对应的文本
     */
    public void sendText(String str) {

    }
    /**
     * 发送文本
     * @param str               对应的文本
     */
    public void sendJson(String str) {
    }

    /**
     * 发送本地的文件
     * @param file      文件对象
     */
    public void sendFile(File file, FileCallback fileCallback) throws FileNotFoundException {
        fileHandler.sendFile(file,fileCallback);
    }
    public void start() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            fileHandler = new FileHandler();
                            pipeline.addLast("ping", new IdleStateHandler(0,4,0, TimeUnit.SECONDS));
                            pipeline.addLast("unpacking", new LengthFieldBasedFrameDecoder(HBinaryProtocol.MAX_LENGTH, 0, 4, 0, 4));
                            pipeline.addLast("decoder", new HBinaryProtocolDecoder());
                            pipeline.addLast("encoder", new HBinaryProtocolEncoder());
                            pipeline.addLast("heart",new HeartBeatClientHandler());
                            pipeline.addLast("file",fileHandler);
                            pipeline.addLast("business", new BossClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(this.host, this.port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }}
