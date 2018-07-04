package top.potens.jnet;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosClient {
    private static final Logger logger = LoggerFactory.getLogger(TestBoosClient.class);

    public static void main(String[] args) {
        MyEventListener listener = new MyEventListener();

        final BossClient bossClient = new BossClient();
        bossClient.connect("127.0.0.1", 31415).addServerEventListener(listener);
        ChannelFuture channelFuture = bossClient.receiveFile(new FileCallback() {
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
        }).start();
        // awaitUninterruptibly
        try {
            channelFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("a", "1");
        RPCHeader test = new RPCHeader("test", stringStringHashMap);
        bossClient.sendRPC(test, new RPCCallback<String>() {
            @Override
            public void succeed(String clients) {
                logger.info(String.valueOf(clients));
            }
            @Override
            public void error(String error) {
                logger.error(error);
            }
        });

        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("channel:", e);
        } finally {
            bossClient.release();
        }
        /*try {
            bossClient.sendFile(new File("D:\\data\\build.zip"),HBinaryProtocol.RECEIVE_ASSIGN,"45bf74d2", new FileCallback() {
//            bossClient.sendFile(new File("D:\\data\\build.zip"), HBinaryProtocol.RECEIVE_SERVER, null, new FileCallback() {
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
        }*/

    }
}
