package top.potens.jnet.broad.event;


import top.potens.jnet.broad.listener.RoleChangeListener;

/**
 * Created by wenshao on 2018/5/23.
 * server监听事件
 */
public class ServerReceiveBroadEvent extends ReceiveBasicBroadEvent {
    @Override
    protected void onJoin(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.sendServerAddress();
    }

    @Override
    protected void onServerDisconnect(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.setRole(RoleChangeListener.ROLE_WORK);
    }

    @Override
    protected void onConsentJoin(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.getRoleChainList().add(message);
        socket.sendRoleChain();
    }
}
