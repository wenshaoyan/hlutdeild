package top.potens.jnet.listener;

/**
 * Created by wenshao on 2018/6/23.
 * 模拟http请求的回调函数
 */
public interface RPCCallback<T>{
    public void succeed(T t);
    public void error(String error);
}
