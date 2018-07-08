package top.potens.jnet.handler;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.common.TypeJudge;
import top.potens.jnet.listener.RPCCallback;
import top.potens.jnet.protocol.HBinaryProtocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/6/23.
 * rpc的请求方式
 *
 */
public class RPCHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private Gson gson = new Gson();
    private Map<Integer, RPCCallback> reqMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        if (protocol.getType() == HBinaryProtocol.TYPE_RPC_RES) {
            if (reqMap.containsKey(protocol.getId())) {
                RPCCallback rpcCallback = reqMap.get(protocol.getId());
                ParameterizedType parameterizedType = (ParameterizedType)rpcCallback.getClass().getGenericInterfaces()[0];
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type type = actualTypeArguments[0];
                String textBody = protocol.getTextBody();
                if (TypeJudge.isStringClass(type)) {
                    rpcCallback.succeed(textBody);
                    return;
                }
                try {
                    Object o = gson.fromJson(textBody, type);
                    rpcCallback.succeed(o);
                }catch (Exception e) {
                    rpcCallback.error("gson from error "+ e.getMessage());
                }
            }
        } else{
            ctx.fireChannelRead(protocol);
        }
    }
    public void addReq(int id, RPCCallback rpcCallback) {
        reqMap.put(id, rpcCallback);
    }
}
