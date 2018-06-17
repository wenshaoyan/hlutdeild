package top.potens.jnet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.handler.ForwardHandler;
import top.potens.jnet.listener.FileCallback;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosServer {
    private static final Logger logger = LogManager.getLogger(TestBoosServer.class);

    public static void main(String[] args) {
        final BossServer bossServer = new BossServer();
        bossServer.fileUpSaveDir("d:\\tmp");
        bossServer.receiveFile(new FileCallback() {
            @Override
            public void start(int id, String path, long size) {
                logger.info("r:start:id" + id + ",path:" + path);
            }
            @Override
            public void process(int id, long size, long process) {
                logger.info("r:process:id:" + id + ",size:" + size + ",process:" + process);
            }

            @Override
            public void end(int id, long size) {
                logger.info("r:end:id:" + id + ",size:" + size);
            }
        });



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
