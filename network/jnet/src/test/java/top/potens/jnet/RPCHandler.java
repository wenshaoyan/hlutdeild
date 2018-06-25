package top.potens.jnet;

import top.potens.jnet.listener.RPCReqHandlerListener;

import java.util.Map;

/**
 * Created by wenshao on 2018/6/25.
 * rpc
 */
public class RPCHandler implements RPCReqHandlerListener {
    public String test(Map<String, String> args) {
        return "111aaa";
    }
}
