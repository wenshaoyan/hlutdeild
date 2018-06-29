package top.potens.teleport.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import io.netty.channel.ChannelFuture;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;
import top.potens.jnet.listener.FileCallback;
import top.potens.teleport.data.EventServiceData;
import top.potens.teleport.data.RpcResponseData;
import top.potens.teleport.util.FileUtil;

/**
 * Created by wenshao on 2018/6/28.
 * service client socket
 */

public class JnetService extends Service {
    private static final int serverListenerPort = 31415;
    private static final Logger logger = LoggerFactory.getLogger(JnetService.class);

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
        final BroadSocket socket = BroadSocket.getInstance();
        socket.setRoleChangeListener(new RoleChangeListener() {
            @Override
            public void onWorkToClient() {
                logger.debug("onWorkToClient");
                List<String> roleChainList = socket.getRoleChainList();
                startJnetBossClient(roleChainList.get(0));
            }

            @Override
            public void onWorkToServer() {
                logger.debug("onWorkToServer");
                startJnetBossService();
                logger.debug("no sync");
                List<String> roleChainList = socket.getRoleChainList();
                startJnetBossClient(roleChainList.get(0));
            }

            @Override
            public void onClientToWork() {
                logger.debug("onClientToWork");
                List<String> roleChainList = socket.getRoleChainList();
                startJnetBossClient(roleChainList.get(0));
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
    private void startJnetBossService() {
        final BossServer bossServer = new BossServer();
        ChannelFuture channelFuture = bossServer.listenerPort(serverListenerPort).setRPCReqListener(new RpcResponseData()).start();
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            logger.error("");
        }
        logger.debug("BoosServer start suc, port=" + bossServer.getPort());
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossServer.release();
        }
    }
    // 启动boss client
    private void startJnetBossClient(String host) {
        EventServiceData listener = new EventServiceData();
        final BossClient bossClient = new BossClient();
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
        } catch (InterruptedException e) {
            logger.error("client start fail", e);
        }
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossClient.release();
        }
    }
}
