package top.potens.teleport.util;

import android.os.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import top.potens.teleport.bean.FriendUserBean;
import top.potens.teleport.constant.HandlerCode;
import top.potens.teleport.data.EventServiceData;

/**
 * Created by wenshao on 2018/7/7.
 * 全局静态对象数据管理
 */
public class XGlobalDataUtil {
    private static final Logger logger = LoggerFactory.getLogger(XGlobalDataUtil.class);
    private final static List<FriendUserBean> friendUserBeans = new ArrayList<>();
    private static List<Handler>  friendUserBeanHandlers = new ArrayList<>();


    public static void addFriendUserBean(FriendUserBean friendUserBean) {
        friendUserBeans.add(friendUserBean);
    }
    public static void cleanFriendUserBean() {
        friendUserBeans.clear();
    }
    public static List<FriendUserBean> getFriendUserBeans() {
        return friendUserBeans;
    }
    public static void addFriendUserBeansListener(Handler handler) {
        friendUserBeanHandlers.add(handler);
    }
    public static void notifyAllFriendUserBeansListener() {
        for (Handler handler : friendUserBeanHandlers) {
            handler.sendEmptyMessage(HandlerCode.DATA_CHANGE);
        }
    }
}
