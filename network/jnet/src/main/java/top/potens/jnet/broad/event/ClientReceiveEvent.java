package top.potens.jnet.broad.event;


import top.potens.jnet.broad.listener.RoleChangeListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 2018/5/23.
 * client监听的事件
 */
public class ClientReceiveEvent extends ReceiveBasicEvent {

    @Override
    protected void onClientExit(String message) {
        System.out.println("onClientExit: " + message);
    }

    @Override
    protected void onSyncRoleChain(String message) {
        String[] split = message.split(",");
        List<String> roleChainList = BroadSocket.getInstance().getRoleChainList();
        roleChainList.clear();
        Collections.addAll(roleChainList, split);
        System.out.println("同步成功:" + roleChainList.toString());
    }

    @Override
    protected void onServerDisconnect(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.setRole(RoleChangeListener.ROLE_WORK);
    }



}
