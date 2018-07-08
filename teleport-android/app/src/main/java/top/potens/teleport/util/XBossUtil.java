package top.potens.teleport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCCallback;
import top.potens.teleport.GlobalApplication;
import top.potens.teleport.R;
import top.potens.teleport.bean.FriendUserBean;
import top.potens.teleport.constant.HeadMapping;
import top.potens.teleport.constant.SpConstant;
import top.potens.teleport.data.EventServiceData;
import top.potens.teleport.data.RpcResponseData;

/**
 * Created by wenshao on 2018/7/2.
 * boss连接管理
 */

public class XBossUtil {
    private static final Logger logger = LoggerFactory.getLogger(XBossUtil.class);

    private static final int serverListenerPort = 31415;
    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, Future> futures = new HashMap<>();


    public static BossServer bossServer;
    public static BossClient bossClient;

    public static boolean isServer() {
        return bossServer != null;
    }
    // 初始化
    public static void init() {
        BroadSocket.setLocalIp(NetworkUtil.getLocalIp(GlobalApplication.getAppContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final BroadSocket socket = BroadSocket.getInstance();
                socket.setRoleChangeListener(new RoleChangeListener() {
                    @Override
                    public void onWorkToClient() {
                        logger.debug("onWorkToClient");
                        stopJnet();
                        boolean isStart = startJnetBossClient(socket.getServerIp());
                        if (!isStart) socket.sendServerDisconnect();
                    }

                    @Override
                    public void onWorkToServer() {
                        stopJnet();
                        logger.debug("onWorkToServer");
                        startJnetBossService();
                        startJnetBossClient("127.0.0.1");
                    }

                    @Override
                    public void onClientToWork() {
                        stopJnet();
                        logger.debug("onClientToWork");
                    }

                    @Override
                    public void onServerToClient() {
                        logger.debug("onServerToClient");

                    }

                    @Override
                    public void onClientToServer() {
                        logger.debug("onClientToServer");
                    }

                });
            }
        }).start();
    }
    // 启动boss service
    private static boolean startJnetBossService() {
        bossServer = new BossServer();
        ChannelFuture channelFuture = bossServer.listenerPort(serverListenerPort).setRPCReqListener(new RpcResponseData()).start();
        try {
            channelFuture.sync();
            logger.debug("BoosServer start suc, port=" + bossServer.getPort());
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                }
            });
            return true;
        } catch (InterruptedException e) {
            logger.error("startJnetBossService:", e);
            return false;
        }
    }

    private static void stopJnetBossService() {
        if (bossServer != null) {
            bossServer.release();
            bossServer = null;
        }
    }

    // 启动boss client
    private static boolean startJnetBossClient(String host) {
        EventServiceData listener = new EventServiceData();
        bossClient = new BossClient();

        bossClient.connect(host, serverListenerPort).addServerEventListener(listener);

        ChannelFuture channelFuture = bossClient.fileUpSaveDir(FileUtil.getFile()).receiveFile(new FileCallback() {
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
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    BroadSocket broadSocket = BroadSocket.getInstance();
                    broadSocket.sendServerDisconnect();
                }
            });
            sendDeviceInfo();
            return true;
        } catch (Exception e) {
            logger.error("client start fail", e);
            return false;
        }
    }

    // 发送设备的相关信息
    private static void sendDeviceInfo() {
        String head = SpUtil.getString(SpConstant.HEAD_KEY, null);
        if (head == null) {
            head = HeadMapping.getRandHeadKey();
            SpUtil.putString(SpConstant.HEAD_KEY, head);
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("model", DeviceUtil.getDeviceModel());
        stringStringHashMap.put("name", DeviceUtil.getDeviceName());
        stringStringHashMap.put("head", head);
        RPCHeader initDeviceInfo = new RPCHeader("_initDeviceInfo", stringStringHashMap);
        XBossUtil.sendRPC(initDeviceInfo,  new RPCCallback<String>() {
            @Override
            public void succeed(String result) {
                logger.info("sendDeviceInfo:" + result);
            }

            @Override
            public void error(String error) {
                logger.error("sendDeviceInfo:" + error);
            }
        });
    }

    private static void stopJnetBossClient() {
        if (bossClient != null) {
            bossClient.release();
            bossClient = null;
        }
    }

    private static void stopJnet() {
        stopJnetBossClient();
        stopJnetBossService();
    }

    public static void sendRPC(final RPCHeader rpcHeader, final RPCCallback rpcCallback) {
        final String jobID = UUID.randomUUID().toString();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable runnable = new Runnable() {
            public void run() {
                if (bossClient != null && bossClient.getConnectStatus() == BossClient.ConnectStatus.SUCCESS) {
                    Future future = futures.get(jobID);
                    if (future != null) future.cancel(true);
                    bossClient.sendRPC(rpcHeader, rpcCallback);
                    countDownLatch.countDown();
                }
            }
        };

        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        Future future = scheduledExecutor.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);
        futures.put(jobID, future);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("sendRPC error:", e);
        }
    }

    // 通用的client变化处理方法
    public static void commonClientChange() {
        RPCHeader rpcHeader = new RPCHeader("getClients", new HashMap<String, String>());
        XBossUtil.sendRPC(rpcHeader, new RPCCallback<List<Client>>() {
            @Override
            public void succeed(List<Client> clients) {
                XGlobalDataUtil.cleanFriendUserBean();
                for (Client client : clients) {
                    FriendUserBean friendUserBean = new FriendUserBean(client.getChannelId(), client.getShowName(), HeadMapping.getHead(client.getImage()));
                    XGlobalDataUtil.addFriendUserBean(friendUserBean);
                }
                XGlobalDataUtil.notifyAllFriendUserBeansListener();
            }

            @Override
            public void error(String s) {
                logger.error("rpc error "+s);
            }
        });
    }

}
