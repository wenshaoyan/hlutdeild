package top.potens.jnet;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.listener.FileCallback;

import java.util.*;

/**
 * Created by wenshao on 2018/6/14.
 * test
 */
public class TestBoosServer {
    private static final Logger logger = LoggerFactory.getLogger(TestBoosClient.class);

    public static void main(String[] args) {
        final BossServer bossServer = new BossServer();
        ChannelFuture channelFuture = bossServer.fileUpSaveDir("d:\\tmp").listenerPort(31415)
                .setRPCReqListener(new RPCHandler()).
                receiveFile(new FileCallback() {
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
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            logger.error("channel:", e);
            return;
        }
        logger.debug("BoosServer start suc, port=" + bossServer.getPort());
        bossServer.broadcastEvent("onMessage", "111111111");


        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("channel:", e);
        } finally {
            bossServer.release();
        }
    }


}
