package com.potens.jnet.broad;


import com.potens.jnet.broad.event.BroadSocket;
import com.potens.jnet.broad.listener.RoleChangeListener;

/**
 * Created by wenshao on 2018/5/23.
 */
public class Main {
    public static void main(String[] args) {
        BroadSocket socket = BroadSocket.getInstance();
        socket.setRoleChangeListener(new RoleChangeListener() {
            @Override
            public void onWorkToClient() {
                System.out.println("onWorkToClient");
            }

            @Override
            public void onWorkToServer() {
                System.out.println("onWorkToServer");

            }

            @Override
            public void onClientToWork() {
                System.out.println("onClientToWork");

            }

            @Override
            public void onServerToClient() {
                System.out.println("onServerToClient");

            }

            @Override
            public void onClientToServer() {
                System.out.println("onClientToServer");
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
                System.out.println("=================voteServerDisconnect start");
                socket.voteServerDisconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
