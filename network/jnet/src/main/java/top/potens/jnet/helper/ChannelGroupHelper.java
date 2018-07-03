package top.potens.jnet.helper;

import io.netty.channel.Channel;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import top.potens.jnet.bean.Client;
import top.potens.jnet.bean.ClientGroup;

import java.util.*;

/**
 * Created by wenshao on 2018/6/12.
 * channel连接管理类
 */
public class ChannelGroupHelper {
    private static final ChannelGroup ALL_CHANNEL = new DefaultChannelGroup("__all", GlobalEventExecutor.INSTANCE);
    private static final Map<String, ChannelGroup> groupMap = new HashMap<>();
    private static final Map<String, ChannelId> channelIdMap = new HashMap<>();
    private static final Map<String, Client> allClient = new HashMap<>();
    private static final Map<String, ClientGroup> allClientGroup = new HashMap<>();

    // 添加一个client
    public static void add(Channel channel) {
        String channelId = channel.id().asShortText();
        if (channelIdMap.containsKey(channelId)) {
            return;
        }
        channelIdMap.put(channelId, channel.id());
        ALL_CHANNEL.add(channel);
        String address = channel.remoteAddress().toString().substring(1);
        Client client = new Client(channelId, address);
        allClient.put(channelId, client);
    }
    // 加入到某个组
    public static boolean join(String channelId, String groupId) {
        if ("__all".equals(groupId)) {
            return false;
        }
        if (!channelIdMap.containsKey(channelId)) {
            return false;
        }
        if (!allClient.containsKey(channelId)) {
            return false;
        }
        Channel channel = ALL_CHANNEL.find(channelIdMap.get(channelId));
        Client client = allClient.get(channelId);
        if (groupMap.containsKey(groupId)) {
            groupMap.get(groupId).add(channel);
            // group 下添加client
            ClientGroup groupId1 = allClientGroup.get(groupId);
            groupId1.setName(groupId1.getName() + "、" + client.getShowName());
            groupId1.getClients().add(client);

        } else {
            ChannelGroup group = new DefaultChannelGroup(groupId, GlobalEventExecutor.INSTANCE);
            group.add(channel);
            groupMap.put(groupId, group);
            // 添加group
            ClientGroup clientGroup = new ClientGroup(groupId);
            clientGroup.setName(client.getShowName());
            ArrayList<Client> clients = new ArrayList<>();
            clients.add(client);
            clientGroup.setClients(clients);
            allClientGroup.put(groupId, clientGroup);
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
        if (ch != null && ch.isActive()) {
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
    public static boolean remove(String channelId) {
        if (!channelIdMap.containsKey(channelId)) {
            return false;
        }
        Channel channel = ALL_CHANNEL.find(channelIdMap.get(channelId));
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
    // 按channelId 获取对应的client
    public static Client getClient(String channelId) {
        return allClient.get(channelId);
    }
    // 获取所有的client
    public static List<Client> getClients() {
        return new ArrayList<>(allClient.values());
    }
    // 按groupId 获取对应的client group对象
    public static ClientGroup getClientGroup(String groupId) {
        return allClientGroup.get(groupId);
    }
    // 获取所有的clientGroup
    public static List<ClientGroup> getClientGroups() {
        return new ArrayList<>(allClientGroup.values());
    }
}
