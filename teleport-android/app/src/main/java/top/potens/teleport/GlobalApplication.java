package top.potens.teleport;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.bootstrap.BossClient;
import top.potens.jnet.bootstrap.BossServer;
import top.potens.jnet.broad.event.BroadSocket;
import top.potens.jnet.broad.listener.RoleChangeListener;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.listener.RPCCallback;
import top.potens.teleport.activity.IndexActivity;
import top.potens.teleport.adapter.FriendAdapter;
import top.potens.teleport.constant.HandlerCode;
import top.potens.teleport.data.EventServiceData;
import top.potens.teleport.data.RpcResponseData;
import top.potens.teleport.util.DeviceUtil;
import top.potens.teleport.util.FileUtil;
import top.potens.teleport.util.NetworkUtil;
import top.potens.teleport.util.ToastUtil;
import top.potens.teleport.util.XBossUtil;


/**
 * Created by Administrator on 2017/9/19.
 * 程序入口
 * 完成app初始化操作
 */

public class GlobalApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(GlobalApplication.class);
    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取Context
        mContext = getApplicationContext();
        XBossUtil.init();
    }


}
