package com.potens.jnet.broad.event;


import com.potens.jnet.broad.listener.RoleChangeListener;

/**
 * Created by wenshao on 2018/5/28.
 */
public class WorkReceiveEvent extends ReceiveBasicEvent {
    @Override
    protected void onServerAddress(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.sendConsentJoin();
        socket.setRole(RoleChangeListener.ROLE_CLIENT);

    }
}
