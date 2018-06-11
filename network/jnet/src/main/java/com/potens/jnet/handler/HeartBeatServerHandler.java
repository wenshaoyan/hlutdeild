package com.potens.jnet.handler;

import com.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by wenshao on 2018/5/6.
 * 接受心跳包
 */
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private int loss_connect_time =0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                System.out.println("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        if (protocol.getFlag()== HBinaryProtocol.FLAG_HEARTBEAT) {
            loss_connect_time = 0;
            System.out.println("server FLAG_HEARTBEAT..");

        } else {
            System.out.println("server channelRead..");
            ctx.fireChannelRead(protocol);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
