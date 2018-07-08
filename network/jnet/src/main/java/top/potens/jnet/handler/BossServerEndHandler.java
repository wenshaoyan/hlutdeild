package top.potens.jnet.handler;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.event.EventServerInform;
import top.potens.jnet.exception.ChannelHeartException;
import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.listener.RPCReqHandlerListener;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Map;

/**
 * Created by wenshao on 2018/5/6.
 * 处理客户端连接、执行异常
 */
public class BossServerEndHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(BossServerEndHandler.class);

    private RPCReqHandlerListener mRPCReqHandlerListener;
    private Gson gson = new Gson();

    public BossServerEndHandler(RPCReqHandlerListener rpcReqHandlerListener) {
        this.mRPCReqHandlerListener = rpcReqHandlerListener;

    }

    // client 连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelGroupHelper.add(ch);
        logger.debug("channelActive:channelId=" + ch.id().asShortText() + " ip:" + ch.remoteAddress() + " connect");

    }

    // channel 断开后的事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String logPrefix = "channelInactive:channelId=" + ctx.channel().id().asShortText() + ":";
        logger.error(logPrefix + " initiative close");
        clientClose(ctx.channel());
    }


    // 功能：读取完毕发送过来的数据之后的操作
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        Channel ch = ctx.channel();
        String logPrefix = "channelInactive:channelId=" + ch.id().asShortText() + ":";
        if (protocol.getType() == HBinaryProtocol.TYPE_RPC_REQ) {
            if (this.mRPCReqHandlerListener != null) {
                Class<? extends RPCReqHandlerListener> aClass = this.mRPCReqHandlerListener.getClass();
                String textBody = protocol.getTextBody();
                try {
                    RPCHeader rpcHeader = gson.fromJson(textBody, RPCHeader.class);
                    String method = rpcHeader.getMethod();
                    Method m = aClass.getMethod(method, String.class, Map.class);
                    Class<?> returnType = m.getReturnType();
                    if (returnType == String.class) {
                        Object o = m.invoke(this.mRPCReqHandlerListener, ch.id().asShortText(), rpcHeader.getBody());
                        HBinaryProtocol protocolRes = HBinaryProtocol.buildReqToRes(protocol, (String) o);
                        ctx.writeAndFlush(protocolRes);
                    } else {
                        logger.error(logPrefix + "method=" + method + " returnType not is String, returnType=" + returnType);
                    }
                } catch (Exception e) {
                    logger.error(logPrefix + ", rpcHead fromJson fail,textBody=" + textBody, e);
                }
            } else {
                logger.error(logPrefix + "mRPCReqHandlerListener is null");
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String logPrefix = "channelInactive:channelId=" + ctx.channel().id().asShortText() + ":";
        if (cause instanceof IOException) {
            logger.error(logPrefix + " exceptionCaught,will close socket:", cause);
            clientClose(ctx.channel());
        } else if (cause instanceof ChannelHeartException) { // 心跳异常
            logger.error(logPrefix + " not heart,will close socket:", cause);
            clientClose(ctx.channel());
        } else {    // 业务异常
            logger.error(logPrefix + " end exception:", cause);
        }
    }
    public void clientClose(Channel ch) {
        String logPrefix = "channelInactive:channelId=" + ch.id().asShortText() + ":";
        String channelId = ch.id().asShortText();
        Client client = ChannelGroupHelper.getClient(channelId);
        ChannelGroupHelper.remove(channelId);
        // 通知所有的client有新的client退出
        ChannelGroupHelper.broadcast(HBinaryProtocol.buildEventAll("onClientExit", client.getAddress()));
        ch.close();
    }

}
