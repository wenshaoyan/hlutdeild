package top.potens.teleport.constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import top.potens.teleport.R;

/**
 * Created by wenshao on 2018/7/8.
 * 头像映射关系
 */
public class HeadMapping {
    private static Map<String, Integer> heads = new HashMap<>();

    static {
        heads.put("head1", R.mipmap.head1);
        heads.put("head2", R.mipmap.head2);
        heads.put("head3", R.mipmap.head3);
        heads.put("head4", R.mipmap.head4);
        heads.put("head5", R.mipmap.head5);
        heads.put("head6", R.mipmap.head6);
        heads.put("head7", R.mipmap.head7);
        heads.put("head8", R.mipmap.head8);
        heads.put("head9", R.mipmap.head9);
        heads.put("head10", R.mipmap.head10);
        heads.put("head11", R.mipmap.head11);
    }

    // 根据key获取对应的head的id   如果key不存在则返回head1
    public static int getHead(String key) {
        if (heads.containsKey(key)) {
            return heads.get(key);
        } else {
            return heads.get("head1");
        }
    }

    // 随机获取
    public static String getRandHeadKey() {
        String[] keys = heads.keySet().toArray(new String[0]);
        Random random = new Random();
        return keys[random.nextInt(keys.length)];
    }
}
