package top.potens.jnet;

import top.potens.jnet.bootstrap.BossServer;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosServer {
    public static void main(String[] args) {
        final BossServer bossServer = new BossServer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                bossServer.listenerPort(31416).start();
            }
        }).start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
