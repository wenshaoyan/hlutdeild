package top.potens.teleport.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import top.potens.teleport.GlobalApplication;

/**
 * Created by wenshao on 2017/3/13.
 * sp操作
 */

public class SpUtil {
    private static Context mContext = GlobalApplication.getAppContext();
    private static SharedPreferences config = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
    ;

    /**
     * 写入
     *
     * @param key   写入名称
     * @param value 写入值
     */
    public static void putBoolean(String key, boolean value) {
        config.edit().putBoolean(key, value).apply();
    }

    /**
     * 读取
     *
     * @param key      读取名称
     * @param defValue 默认值
     * @return 对应的值
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return config.getBoolean(key, defValue);
    }

    /**
     * 写入
     *
     * @param key   写入名称
     * @param value 写入值
     */
    public static void putString(String key, String value) {
        config.edit().putString(key, value).apply();
    }

    /**
     * 读取
     *
     * @param key      读取名称
     * @param defValue 默认值
     * @return 对应的值
     */
    public static String getString(String key, String defValue) {
        return config.getString(key, defValue);
    }

    public static void remove(String key) {
        config.edit().remove(key).apply();
    }

    public static int getInt(String key, int defValue) {
        return config.getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        config.edit().putInt(key, value).apply();
    }

}
