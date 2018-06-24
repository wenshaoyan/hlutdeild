package top.potens.jnet.handler;

import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

/**
 * Created by wenshao on 2018/5/6.
 * 处理客户端连接、执行异常
 */
public class BossServerEndHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    // client 连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelGroupHelper.add(ch);
        //logger.debug("channelId=" + ch.id().asShortText() + " ip:"+ ch.remoteAddress() + " connect");

    }

    // channel 断开后的事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelGroupHelper.remove(ch);
        // logger.debug("channelId=" + ch.id().asShortText() + " ip:"+ ch.remoteAddress() + " close");
    }


    // 功能：读取完毕发送过来的数据之后的操作
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        /*if (hBinaryProtocol.getType() == HBinaryProtocol.TYPE_RPC_REQ) {
            // System.out.println(hBinaryProtocol.getTextBody());
            // System.out.println(hBinaryProtocol.getBody().length);
        }*/
        if (protocol.getType() == HBinaryProtocol.TYPE_RPC_REQ) {
            System.out.println(protocol.getTextBody());
            HBinaryProtocol protocolRes = HBinaryProtocol.buildReqToRes(protocol, "1");
            ctx.writeAndFlush(protocolRes);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // logger.error("exceptionCaught",cause);
        ctx.close();
    }

}
