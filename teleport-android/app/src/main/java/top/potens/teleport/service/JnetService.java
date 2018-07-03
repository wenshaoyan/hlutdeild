package top.potens.teleport.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.netty.channel.ChannelFuture;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCCallback;
import top.potens.teleport.data.EventServiceData;
import top.potens.teleport.data.RpcResponseData;
import top.potens.teleport.util.DeviceUtil;
import top.potens.teleport.util.FileUtil;
import top.potens.teleport.util.NetworkUtil;
import top.potens.teleport.util.XBossUtil;

/**
 * Created by wenshao on 2018/6/28.
 * service client socket
 */

public class JnetService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(JnetService.class);
    private static final int serverListenerPort = 31415;
    private BossServer bossServer;
    private BossClient bossClient;

    // 绑定服务时才会调用
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
    // 如果服务已在运行，则不会调用此方法。该方法只被调用一次
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        BroadSocket.setLocalIp(NetworkUtil.getLocalIp(this.getApplicationContext()));
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
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
          //  }
        //}).start();
    }

    // 每次通过startService()方法启动Service时都会被回调。
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // 服务销毁时的回调
    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        super.onDestroy();
    }

    // 启动boss service
    private boolean startJnetBossService() {
        bossServer = new BossServer();
        ChannelFuture channelFuture = bossServer.listenerPort(serverListenerPort).setRPCReqListener(new RpcResponseData()).start();
        try {
            channelFuture.sync();
            XBossUtil.bossServer = bossServer;
            logger.debug("BoosServer start suc, port=" + bossServer.getPort());
            return true;
        } catch (InterruptedException e) {
            logger.error("startJnetBossService:", e);
            return false;
        }
    }

    private void stopJnetBossService() {
        if (bossServer != null) {
            bossServer.release();
            bossServer = null;
        }
    }

    // 启动boss client
    private boolean startJnetBossClient(String host) {
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
            XBossUtil.bossClient = bossClient;
            sendDeviceInfo();
            return true;
        } catch (Exception e) {
            logger.error("client start fail", e);
            return false;
        }
    }
    // 发送设备的相关信息
    private  void sendDeviceInfo() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("model", DeviceUtil.getDeviceModel());
        stringStringHashMap.put("name", DeviceUtil.getDeviceName());
        RPCHeader initDeviceInfo = new RPCHeader("_initDeviceInfo", stringStringHashMap);
        bossClient.sendRPC(initDeviceInfo, new RPCCallback<String>() {
            @Override
            public void succeed(String result) {
                logger.info("sendDeviceInfo:"+ result);
            }

            @Override
            public void error(String error) {
                logger.error("sendDeviceInfo:"+ error);
            }
        });
    }
    private void stopJnetBossClient(){
        if (bossClient != null) {
            bossClient.release();
            bossClient = null;
        }
    }
    private void stopJnet() {
        stopJnetBossClient();
        stopJnetBossService();
    }

}
