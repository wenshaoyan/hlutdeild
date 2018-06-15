package top.potens.jnet;

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

    public static void main(String[] args) {
        final BossClient bossClient = new BossClient();
        bossClient.connect("127.0.0.1", 31416);
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
            bossClient.sendFile(new File("D:\\迅雷下载\\Ylmf_Ghost_Win7_SP1_x64_2018_0210.iso"), new FileCallback() {
                @Override
                public void apply(HBinaryProtocol hBinaryProtocol, int id, String path, long size) {
                    //bossClient
                }

                @Override
                public void process(HBinaryProtocol hBinaryProtocol, int id, long size, int process) {

                }

                @Override
                public void end(HBinaryProtocol hBinaryProtocol, int id, long size) {

                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
