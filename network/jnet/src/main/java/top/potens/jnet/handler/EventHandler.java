package top.potens.jnet.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.potens.jnet.event.EventSource;
import top.potens.jnet.protocol.HBinaryProtocol;

/**
 * Created by wenshao on 2018/6/24.
 * 接收通知事件
 */
public class EventHandler  extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private EventSource mEventSource;
    public EventHandler(EventSource eventSource) {
        this.mEventSource = eventSource;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        if (protocol.getType() == HBinaryProtocol.TYPE_EVENT) {
            // 找到第一个?出现的地方  拆分方法名和参数
            int i = protocol.getTextBody().indexOf('?');
            if (i != -1) {  //
                String method = protocol.getTextBody().substring(0, i);
                String args = protocol.getTextBody().substring(i+1);
                this.mEventSource.message(method, args);
            }

        } else {
            ctx.fireChannelRead(protocol);
        }
    }
}
