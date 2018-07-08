package top.potens.teleport.data;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.RPCHeader;
import top.potens.jnet.listener.RPCCallback;
import top.potens.teleport.R;
import top.potens.teleport.bean.FriendUserBean;
import top.potens.teleport.constant.HandlerCode;
import top.potens.teleport.util.ToastUtil;
import top.potens.teleport.util.XBossUtil;
import top.potens.teleport.util.XGlobalDataUtil;

/**
 * Created by wenshao on 2018/6/23.
 * service通知到client的事件
 * 自定义监听器中通过EventObject判断事件来源，所以前面说EventObject是起路由功能。
 */

public class EventServiceData implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceData.class);

    // 新的client加入事件
    public void onClientJoin(EventObject e, String jsonString) {
        XBossUtil.commonClientChange();
    }

    // client退出事件
    public void onClientExit(EventObject e, String jsonString) {
        XBossUtil.commonClientChange();
    }

    // 组内有新的client加入事件
    public void onGroupClientJoin(EventObject e, String jsonString) {
        // System.out.println(args);
    }

    // 组内有client退出事件
    public void onGroupClientExit(EventObject e, String jsonString) {

    }

}