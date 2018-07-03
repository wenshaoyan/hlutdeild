package top.potens.jnet;

import top.potens.jnet.listener.RPCReqHandlerListener;

import java.util.Map;

/**
 * Created by wenshao on 2018/6/25.
 * rpc
 */
public class RPCHandler implements RPCReqHandlerListener {
    public String test(String channelId ,Map<String, String> args) {

        return "[{\"address\":\"127.0.0.1:44662\",\"channelId\":\"5cf272ae\",\"deviceModel\":\"Redmi Note 4X\",\"deviceName\":\"红米手机\",\"showName\":\"红米手机\"}]";
    }
}
