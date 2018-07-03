package top.potens.teleport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.listener.RPCCallback;

/**
 * Created by wenshao on 2018/7/2.
 * boss连接管理
 */

public class XBossUtil {
    private static final Logger logger = LoggerFactory.getLogger(XBossUtil.class);
    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, Future> futures = new HashMap<>();


    public static BossServer bossServer;
    public static BossClient bossClient;

    public static boolean isServer() {
        return bossServer != null;
    }

    public static void sendRPC(final RPCHeader rpcHeader, final RPCCallback rpcCallback) {
        final String jobID = "my_job_1";

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable runnable = new Runnable() {
            public void run() {
                logger.debug("111");
                if (bossClient != null) {
                    Future future = futures.get(jobID);
                    if (future != null) future.cancel(true);
                    bossClient.sendRPC(rpcHeader, rpcCallback);
                    countDownLatch.countDown();
                    logger.debug("222");
                }
            }
        };

        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        Future future = scheduledExecutor.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);
        futures.put(jobID, future);
        try {
            countDownLatch.await();
            logger.debug("sendRPC suc");
        } catch (InterruptedException e) {
            logger.error("sendRPC error:", e);
        }
    }


}
