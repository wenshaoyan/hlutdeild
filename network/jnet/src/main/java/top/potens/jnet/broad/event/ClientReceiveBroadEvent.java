package top.potens.jnet.broad.event;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.broad.listener.RoleChangeListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 2018/5/23.
 * client监听的事件
 */
public class ClientReceiveBroadEvent extends ReceiveBasicBroadEvent {
    private static final Logger logger = LoggerFactory.getLogger(ClientReceiveBroadEvent.class);
    @Override
    protected void onClientExit(String message) {
        logger.debug("onClientExit: " + message);
    }

    @Override
    protected void onSyncRoleChain(String message) {
        String[] split = message.split(",");
        List<String> roleChainList = BroadSocket.getInstance().getRoleChainList();
        roleChainList.clear();
        Collections.addAll(roleChainList, split);
        logger.debug("同步成功:" + roleChainList.toString());
    }

    @Override
    protected void onServerDisconnect(String message) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.setRole(RoleChangeListener.ROLE_WORK);
    }



}
