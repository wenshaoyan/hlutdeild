package top.potens.jnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;

import java.net.InetAddress;

/**
 * Created by wenshao on 2018/5/23.
 */
public class TestBroad {
    private static final Logger logger = LoggerFactory.getLogger(TestBroad.class);
    private static String localIp;
    static {
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.error("get local ip error: ", e);
        }
    }
    public static void main(String[] args) {
        BroadSocket.setLocalIp(localIp);
        final BroadSocket socket = BroadSocket.getInstance();
        socket.setRoleChangeListener(new RoleChangeListener() {
            @Override
            public void onWorkToClient() {
                logger.debug("onWorkToClient");
                logger.debug(socket.getServerIp());
            }

            @Override
            public void onWorkToServer() {
                logger.debug("onWorkToServer");

            }

            @Override
            public void onClientToWork() {
                logger.debug("onClientToWork:");

            }

            @Override
            public void onServerToClient() {
                logger.debug("onServerToClient:"+socket.getServerIp());

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
