package top.potens.jnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;

/**
 * Created by wenshao on 2018/5/23.
 */
public class TestBroad {
    private static final Logger logger = LoggerFactory.getLogger(TestBroad.class);

    public static void main(String[] args) {
        BroadSocket.setLocalIp("127.0.0.1");
        BroadSocket socket = BroadSocket.getInstance();
        socket.setRoleChangeListener(new RoleChangeListener() {
            @Override
            public void onWorkToClient() {
                logger.debug("onWorkToClient");
            }

            @Override
            public void onWorkToServer() {
                logger.debug("onWorkToServer");

            }

            @Override
            public void onClientToWork() {
                logger.debug("onClientToWork");

            }

            @Override
            public void onServerToClient() {
                logger.debug("onServerToClient");

            }

            @Override
            public void onClientToServer() {
                logger.debug("onClientToServer");
            }

        });
        try {
            Thread.sleep(5000);
            /*if (socket.getRole() == RoleChangeListener.ROlE_SERVER) {
                Thread.sleep(15000);
                socket.disposeClientExit("1");
                Thread.sleep(15000);
                socket.disposeClientExit("2");
                Thread.sleep(15000);
                socket.disposeClientExit("2");
            }*/
            if (socket.getRole() == RoleChangeListener.ROLE_CLIENT) {
                logger.debug("voteServerDisconnect start");
                socket.voteServerDisconnect();
            }
        }catch (Exception e){
            logger.error("sleep:", e);
        }

    }
}
