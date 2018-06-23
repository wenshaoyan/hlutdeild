package top.potens.jnet.helper;

import io.netty.channel.Channel;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;

/**
 * Created by wenshao on 2018/6/12.
 * channel连接管理类
 */
public class ChannelGroupHelper {
    private static final ChannelGroup ALL_CHANNEL = new DefaultChannelGroup("__all", GlobalEventExecutor.INSTANCE);
    private static final Map<String, ChannelGroup> groupMap = new HashMap<>();
    private static final Map<String, ChannelId> channelIdMap = new HashMap<>();


    // 添加一个client
    public static void add(Channel channel) {
        channelIdMap.put(channel.id().asShortText(), channel.id());
        ALL_CHANNEL.add(channel);
    }

    // 加入到某个组
    public static boolean join(Channel channel, String groupId) {
        //ChannelId channelId = DefaultChannelId.newInstance();

        if ("__all".equals(groupId)) {
            return false;
        }
        if (groupMap.containsKey(groupId)) {
            groupMap.get(groupId).add(channel);
        } else {
            ChannelGroup group = new DefaultChannelGroup(groupId, GlobalEventExecutor.INSTANCE);
            group.add(channel);
            groupMap.put(groupId, group);
        }
        return true;
    }

    // 广播至所有客户端
    public static ChannelGroupFuture broadcast(Object msg) {
        return ALL_CHANNEL.writeAndFlush(msg);
    }

    // 按组进行广播播
    public static ChannelGroupFuture broadcast(Object msg, String groupId) {
        return groupMap.get(groupId).writeAndFlush(msg);
    }
    // 指定channelId发送
    public static boolean sendAssign(Object msg, String stringChannelId) {
        ChannelId channelId = channelIdMap.get(stringChannelId);
        if (channelId == null) {
            // TODO channelId为null 的错误提示
            return false;
        }
        Channel ch = ALL_CHANNEL.find(channelId);
        if (ch != null && ch.isActive()){
            ch.writeAndFlush(msg);
            return true;
        }
        return false;
    }

    // 刷新所有group缓冲区
    public static ChannelGroup flush() {
        return ALL_CHANNEL.flush();
    }

    // 刷新对应的group的缓冲区
    public static ChannelGroup flush(String groupId) {
        return groupMap.get(groupId).flush();
    }

    // 删除所有group对应的channel
    public static boolean remove(Channel channel) {
        // 遍历group
        for (Map.Entry<String, ChannelGroup> item : groupMap.entrySet()) {
            if (item.getValue().remove(channel)) {
                break;
            }
        }
        return ALL_CHANNEL.remove(channel);
    }

    // 删除指定group的channel
    public static boolean remove(Channel channel, String groupId) {
        return groupMap.get(groupId).remove(channel);
    }

    // 所有client总的个数
    public static int size() {
        return ALL_CHANNEL.size();
    }

    // 指定组的client的个数
    public static int size(String groupId) {
        return groupMap.get(groupId).size();

    }
}
