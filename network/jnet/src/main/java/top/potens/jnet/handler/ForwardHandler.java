package top.potens.jnet.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.protocol.HBinaryProtocol;

/**
 * Created by wenshao on 2018/6/16.
 * 消息转发转发
 */
public class ForwardHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        Channel ch = ctx.channel();
        byte receive = protocol.getReceive();
        String receiveId = protocol.getReceiveId();
        if (receive == HBinaryProtocol.RECEIVE_ASSIGN) {    // 发送给指定的client
            ChannelGroupHelper.sendAssign(protocol, receiveId);
        } else if (receive == HBinaryProtocol.RECEIVE_GROUP) {
            ChannelGroupHelper.broadcast(protocol, receiveId);
        } else {
            ctx.fireChannelRead(protocol);
        }
        if (HBinaryProtocol.TYPE_FILE_APPLY == protocol.getType()) {
            HBinaryProtocol applyHBinaryProtocol = HBinaryProtocol.buildSimpleText(protocol.getId(), protocol.getTextBody(), protocol.getReceive(), protocol.getReceiveId(), HBinaryProtocol.TYPE_FILE_AGREE);
            ctx.writeAndFlush(applyHBinaryProtocol);
        }
    }
}
