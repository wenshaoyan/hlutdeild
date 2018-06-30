package top.potens.jnet.broad.event;


import top.potens.jnet.broad.listener.RoleChangeListener;

/**
 * Created by wenshao on 2018/5/28.
 */
public class WorkReceiveBroadEvent extends ReceiveBasicBroadEvent {
    @Override
    protected void onServerAddress(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.setServerIp(message);
        socket.sendConsentJoin();
        socket.setRole(RoleChangeListener.ROLE_CLIENT);

    }
}
