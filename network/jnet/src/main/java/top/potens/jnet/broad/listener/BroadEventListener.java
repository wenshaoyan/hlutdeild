package top.potens.jnet.broad.listener;

/**
 * deny_disconnect
 * Created by wenshao on 2018/5/23.
 * broad socket接受到消息
 */
public interface BroadEventListener {
    // 每条消息总的大小
    public static final int MESSAGE_TOOL_BYTE = 1024 * 4;
    // work发出: 有work加入的事件
    public static final byte EVENT_W_JOIN = 0x01;
    // server发出: server的管理ip
    public static final byte EVENT_S_MANAGE_ADDRESS = 0x02;
    // server发出: 同步当前局域网的角色链
    public static final byte EVENT_S_SYNC_ROLE_CHAIN = 0x03;
    // server发出: 有client退出的事件
    public static final byte EVENT_S_CLIENT_EXIT = 0x04;
    // client发出：server连接不上
    public static final byte EVENT_C_SERVER_DISCONNECT = 0x05;
    // client发出: 同意加入
    public static final byte EVENT_C_CONSENT_JOIN = 0x06;


    public void onMessage(byte[] bytes);
}
