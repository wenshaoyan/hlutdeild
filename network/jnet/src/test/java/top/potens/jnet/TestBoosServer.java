package top.potens.jnet;

import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.listener.FileCallback;

import java.util.*;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosServer {
   // private static final Logger logger = LogManager.getLogger(TestBoosServer.class);

    public static void main(String[] args) {
        // Logger.addLogAdapter(new AndroidLogAdapter());
        final BossServer bossServer = new BossServer();
        bossServer.fileUpSaveDir("d:\\tmp");

        bossServer.receiveFile(new FileCallback() {
            @Override
            public void start(int id, String path, long size) {

                System.out.println("r:start:id" + id + ",path:" + path);
            }
            @Override
            public void process(int id, long size, long process) {
                System.out.println("r:process:id:" + id + ",size:" + size + ",process:" + process);
            }

            @Override
            public void end(int id, long size) {
                System.out.println("r:end:id:" + id + ",size:" + size);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                bossServer.listenerPort(31416).start();
            }
        }).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bossServer.broadcastEvent("onMessage", "111111111");
    }
    public void a(){
    }

}
