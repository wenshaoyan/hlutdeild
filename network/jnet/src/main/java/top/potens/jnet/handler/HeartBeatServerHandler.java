package top.potens.jnet.handler;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by wenshao on 2018/5/6.
 * 接受心跳包
 */
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);
    private int loss_connect_time = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel ch = ctx.channel();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                logger.warn("channelId=" + ch.id().asShortText() + " channel not send heard,loss_connect_time=" + loss_connect_time);
                if (loss_connect_time > 2) {
                    logger.error("channelId=" + ch.id().asShortText() + " channel not active");
                    ch.close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        Channel ch = ctx.channel();
        if (protocol.getFlag() == HBinaryProtocol.FLAG_HEARTBEAT) {
            loss_connect_time = 0;
            logger.debug("channelId=" + ch.id().asShortText() + " receive client heard");
        } else {
            ctx.fireChannelRead(protocol);
        }
    }

}
