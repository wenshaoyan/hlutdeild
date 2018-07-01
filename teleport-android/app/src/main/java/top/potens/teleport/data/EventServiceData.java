package top.potens.teleport.data;


import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/6/23.
 * service通知到client的事件
 * 自定义监听器中通过EventObject判断事件来源，所以前面说EventObject是起路由功能。
 */

public class EventServiceData implements EventListener {
    // 新的client加入事件
    public void onClientJoin(EventObject e, String jsonString) {
        // System.out.println(args);
    }
    // client退出事件
    public void onClientExit(EventObject e, String jsonString) {

    }
    // 组内有新的client加入事件
    public void onGroupClientJoin(EventObject e, String jsonString) {
        // System.out.println(args);
    }
    // 组内有client退出事件
    public void onGroupClientExit(EventObject e, String jsonString) {

    }

}