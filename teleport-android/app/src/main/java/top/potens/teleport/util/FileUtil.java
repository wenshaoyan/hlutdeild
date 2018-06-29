package top.potens.teleport.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by wenshao on 2018/6/28.
 * 文件工具类
 */

public class FileUtil {
    private static final String extDir = Environment.getExternalStorageDirectory() + "/teleport";
    private static final String cacheDir = extDir + "/cache";
    private static final String fileDir = extDir + "/file";

    private static boolean init() {
        File extDirFile = new File(extDir);
        return extDirFile.mkdir();
    }
    // 缓存目录
    public static String getCache() {
        init();
        File cache = new File(cacheDir);
        boolean isM = cache.mkdir();
        return isM ? cacheDir : null;
    }
    // 文件保存目录
    public static String getFile() {
        init();
        File cache = new File(fileDir);
        boolean isM = cache.mkdir();
        return isM ? fileDir : null;
    }
}
