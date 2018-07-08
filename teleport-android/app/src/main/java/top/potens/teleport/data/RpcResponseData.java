package top.potens.teleport.data;

import com.google.gson.Gson;

import top.potens.jnet.bean.Client;
import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.listener.RPCReqHandlerListener;
import top.potens.jnet.protocol.HBinaryProtocol;

import java.util.Map;

/**
 * Created by wenshao on 2018/6/25.
 * service监听到 client的rpc请求的响应
 */
public class RpcResponseData implements RPCReqHandlerListener {
    private Gson gson = new Gson();
    //  初始化 同步设备信息
    public String _initDeviceInfo(String channelId, Map<String, String> args) {
        String model = args.get("model");
        String name = args.get("name");
        String head = args.get("head");
        Client client = ChannelGroupHelper.getClient(channelId);
        client.setDeviceName(name);
        client.setDeviceModel(model);
        client.setImage(head);
        // 通知所有的client有新的client加入
        ChannelGroupHelper.broadcast(HBinaryProtocol.buildEventAll("onClientJoin", client.getAddress()));
        return "ok";
    }
    // 获取所有client
    public String getClients(String channelId, Map<String, String> args) {
        return gson.toJson(ChannelGroupHelper.getClients());
    }
}
