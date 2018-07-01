package top.potens.teleport.data;

import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.listener.RPCReqHandlerListener;

import java.util.Map;

/**
 * Created by wenshao on 2018/6/25.
 * service监听到 client的rpc请求的响应
 */
public class RpcResponseData implements RPCReqHandlerListener {
    // 同步设备信息
    public String initDeviceInfo(String channelId, Map<String, String> args) {
        String model = args.get("model");
        String name = args.get("name");
        ChannelGroupHelper.
        return "ok";
    }
}
