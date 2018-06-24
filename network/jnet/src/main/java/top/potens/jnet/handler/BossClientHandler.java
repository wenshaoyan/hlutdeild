package top.potens.jnet.handler;

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
public class BossClientHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HBinaryProtocol protocol) throws Exception {

        /*if (protocol.getType() == HBinaryProtocol.TYPE_RPC_REQ) {

        }*/


    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}