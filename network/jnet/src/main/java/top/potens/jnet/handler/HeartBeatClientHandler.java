package top.potens.jnet.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Date;

/**
 * Created by wenshao on 2018/5/6.
 * 发送心跳包
 */
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatClientHandler.class);
    private static final int TRY_TIMES = 3;

    private int currentTime = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("heard active");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.error("heard stop");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.debug("heard send");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                currentTime++;
                ctx.writeAndFlush(HBinaryProtocol.buildHeartbeat());
            }
        }
    }
}
