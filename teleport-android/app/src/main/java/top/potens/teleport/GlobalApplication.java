package top.potens.teleport;

import android.app.Application;
import android.content.Context;

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
    }

}
