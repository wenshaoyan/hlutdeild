package top.potens.jnet.listener;

/**
 * Created by wenshao on 2018/6/27.
 * channel状态改变监听
 */
public interface ChannelStatusChangeListener {
    // channel激活
    public void channelActive();
    // channel断开
    public void channelInactive();
}
