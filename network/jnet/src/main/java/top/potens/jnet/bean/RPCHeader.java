package top.potens.jnet.bean;

import top.potens.jnet.protocol.HBinaryProtocol;

import java.util.Map;
import java.util.UUID;

/**
 * Created by wenshao on 2018/6/23.
 * rpc请求的body
 */
public class RPCHeader {
    private String method;
    private Map<String, String> body;
    private String id;


    private static String getUUID() {
       return UUID.randomUUID().toString().replace("-", "");
    }

    public RPCHeader(String method, Map<String, String> body) {
        this.method = method;
        this.body = body;
        this.id = getUUID();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        return "RPCHeader{" +
                "method='" + method + '\'' +
                ", body=" + body +
                ", id='" + id + '\'' +
                '}';
    }
}

