package top.potens.jnet.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wenshao on 2018/5/6.
 */
public class BossClientEndHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(BossClientEndHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HBinaryProtocol protocol) throws Exception {



    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) { // 连接断开
            ctx.close();
        } else {    // 业务异常 不需要断开
            logger.error("end exception:", cause);
        }
    }
}