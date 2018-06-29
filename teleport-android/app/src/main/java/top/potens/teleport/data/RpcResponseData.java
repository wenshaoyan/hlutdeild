package top.potens.teleport.data;

import top.potens.jnet.listener.RPCReqHandlerListener;

import java.util.Map;

/**
 * Created by wenshao on 2018/6/25.
 * service监听到 client的rpc请求的响应
 */
public class RpcResponseData implements RPCReqHandlerListener {
    public String test(Map<String, String> args) {
        return "111aaa";
    }
}
