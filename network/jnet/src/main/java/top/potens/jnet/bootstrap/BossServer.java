package top.potens.jnet.bootstrap;

import top.potens.jnet.handler.BossServerHandler;
import top.potens.jnet.handler.HeartBeatServerHandler;
import top.potens.jnet.listener.FileCallback;
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

import java.io.File;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshao on 2018/5/6.
 * 负责心跳检查、普通消息传输
 * 端口31416
 */
public class BossServer {
    // 监听端口
    private int port;
    // 文件接收保存的目录
    private String fileUpSaveDir;

    public BossServer() {
        initDefault();
    }

    private void initDefault() {
        this.port = 31416;
        this.fileUpSaveDir = "/d/tmp";
    }

    ;

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

    // ==============================

    public void start() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(this.port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("ping", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast("unpacking", new LengthFieldBasedFrameDecoder(HBinaryProtocol.MAX_LENGTH, 0, 4, 0, 4));
                            pipeline.addLast("decoder", new HBinaryProtocolDecoder());
                            pipeline.addLast("encoder", new HBinaryProtocolEncoder());
                            pipeline.addLast("heart",new HeartBeatServerHandler());
                            pipeline.addLast("business",new BossServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind().sync();
            System.out.println("Server start listen at " + this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
