package top.potens.teleport;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import top.potens.teleport.service.JnetService;


/**
 * Created by Administrator on 2017/9/19.
 * 程序入口
 * 完成app初始化操作
 */

public class GlobalApplication extends Application {
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取Context
        mContext = getApplicationContext();
        startJnetService();
    }

    // 启动jnet服务
    private void startJnetService() {
        Intent intent = new Intent(mContext, JnetService.class);
        startService(intent);
    }

}
