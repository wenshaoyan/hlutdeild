package top.potens.jnet;


import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/6/23.
 * 自定义监听器中通过EventObject判断事件来源，所以前面说EventObject是起路由功能。
 */

public class MyEventListener implements EventListener {
    public void onMessage(EventObject e, String args) {
        System.out.println(args);
    }
    public void onClientSync(EventObject e, String jsonString) {

    }

}