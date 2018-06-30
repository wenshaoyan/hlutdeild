package top.potens.jnet.broad.event;


import top.potens.jnet.broad.listener.BroadEventListener;

import java.io.UnsupportedEncodingException;

/**
 * Created by wenshao on 2018/5/23.
 * 监听事件基类
 */
public class ReceiveBasicBroadEvent implements BroadEventListener {
    private static final String DEFAULT_TEXT_CHARSET = "UTF-8";
    private String byteToString(byte[] bytes) {
        try {
            return new String(bytes, DEFAULT_TEXT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    @Override
    public void onMessage(byte[] buf) {
        //byteToString(bytes);
        byte e = buf[0];
        int endIndex = 1;
        // 找出ASCII为0的位置
        for (int i = 1; i < buf.length; i++) {
            if (buf[i] == 0x0) {
                endIndex = i;
                break;
            }
        }
        // 取出之后的字节
        byte[] data = new byte[endIndex - 1];

        System.arraycopy(buf, 1, data, 0, endIndex - 1);
        String message = byteToString(data);
        // byte清空
        switch (e) {
            case BroadEventListener.EVENT_W_JOIN:
                this.onJoin(message);
                break;
            case BroadEventListener.EVENT_S_MANAGE_ADDRESS:
                this.onServerAddress(message);
                break;
            case BroadEventListener.EVENT_S_SYNC_ROLE_CHAIN:
                this.onSyncRoleChain(message);
                break;
            case BroadEventListener.EVENT_S_CLIENT_EXIT:
                this.onClientExit(message);
                break;
            case BroadEventListener.EVENT_C_SERVER_DISCONNECT:
                this.onServerDisconnect(message);
                break;
            case BroadEventListener.EVENT_C_CONSENT_JOIN:
                this.onConsentJoin(message);
                break;
            default:
                break;

        }
    }

    protected void onClientExit(String message) {

    }

    protected void onSyncRoleChain(String message) {

    }

    protected void onServerAddress(String message) {

    }

    protected void onJoin(String message) {

    }
    protected void onServerDisconnect(String message) {

    }
    protected void onConsentJoin(String message) {

    }
}
