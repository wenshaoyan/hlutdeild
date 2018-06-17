package top.potens.jnet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.common.TypeConvert;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.protocol.HBinaryProtocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.TreeMap;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosClient {
    private static final Logger logger = LogManager.getLogger(TestBoosClient.class);

    public static void main(String[] args) {
        final BossClient bossClient = new BossClient();
        bossClient.connect("127.0.0.1", 31416);
        bossClient.receiveFile(new FileCallback() {
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
                bossClient.start();
            }
        }).start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
//            bossClient.sendFile(new File("D:\\data\\build.zip"),HBinaryProtocol.RECEIVE_ASSIGN,"45bf74d2", new FileCallback() {
            bossClient.sendFile(new File("D:\\data\\build.zip"),HBinaryProtocol.RECEIVE_SERVER,null, new FileCallback() {
                @Override
                public void start(int id, String path, long size) {
                    logger.info("s:start:id" + id + ",path:" + path);
                }
                @Override
                public void process(int id, long size, long process) {
                    logger.info("s:process:id:" + id + ",size:" + size + ",process:" + process);
                }

                @Override
                public void end(int id, long size) {
                    logger.info("s:end:id:" + id + ",size:" + size);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
