package top.potens.jnet.broad.event;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.broad.listener.RoleChangeListener;
import top.potens.jnet.broad.runnable.UDPRunnable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wenshao on 2018/5/28.
 * send and receiver socket
 */
public class BroadSocket {
    private static final Logger logger = LoggerFactory.getLogger(BroadSocket.class);

    private static BroadSocket socket;
    private String host = "239.0.0.106";
    private int port = 31415;
    private InetAddress inetAddress;
    private MulticastSocket mus;
    private ReceiveBasicEvent receiveBasicEvent;
    private static int PERF_TIME = 1000;        // 性能时间

    static {
        Properties props = System.getProperties();
        String systemName = props.getProperty("os.name").toLowerCase();
        String systemWindowRe = ".*window.*";

        // window +250  android + 500 linux +50
        if (Pattern.matches(systemWindowRe, systemName)) {
            PERF_TIME += 200;
        }
        Random random = new Random();
        int i = random.nextInt(100);
        PERF_TIME += i;
        System.out.println("PERF_TIME = " + PERF_TIME);
    }

    // 内网ip string
    private static String localIp = null;
    // 内网ip byte
    private static byte[] localIpByte = null;

    static {
        try {
            // localIp = InetAddress.getLocalHost().getHostAddress();
//             localIp = String.valueOf(new Date().getTime());
            localIp = "3";
            localIpByte = localIp.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 当前的角色
    private byte role = RoleChangeListener.ROLE_WORK;

    // work状态的定时器 如果指定时间收到了EVENT_SERVER_ADDRESS则取消定时器 否则work->server
    private Timer workStatusTimer = null;
    // 当前的角色链
    private List<String> roleChainList = new ArrayList<String>();

    private RoleChangeListener roleChangeListener;

    private BroadSocket() {
        initSocket();
    }

    public static BroadSocket getInstance() {
        if (socket == null) socket = new BroadSocket();
        return socket;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public MulticastSocket getMus() {
        return mus;
    }

    public static String getLocalIp() {
        return localIp;
    }

    // 设置回复监听
    private void setReceiveListener(ReceiveBasicEvent e) {
        this.receiveBasicEvent = e;
    }

    // 设置role change监听
    public void setRoleChangeListener(RoleChangeListener listener) {
        initWorkRole();
        this.roleChangeListener = listener;
    }

    public List<String> getRoleChainList() {
        return roleChainList;
    }


    public void setRoleChainList(List<String> roleChainList) {
        this.roleChainList = roleChainList;
    }

    public byte getRole() {
        return role;
    }

    public void setRole(byte role) {
        if (this.getRole() == RoleChangeListener.ROLE_WORK && role == RoleChangeListener.ROlE_SERVER) {   // work -> server
            this.roleChangeListener.onWorkToServer();
            workStatusTimer.cancel();
            setReceiveListener(new ServerReceiveEvent());
            sendServerAddress();
            roleChainList.clear();
            roleChainList.add(localIp);
        } else if (this.getRole() == RoleChangeListener.ROLE_WORK && role == RoleChangeListener.ROLE_CLIENT) {    // work -> client
            this.roleChangeListener.onWorkToClient();
            workStatusTimer.cancel();
            setReceiveListener(new ClientReceiveEvent());
        } else if (this.getRole() == RoleChangeListener.ROLE_CLIENT && role == RoleChangeListener.ROlE_SERVER) {    // client -> server
            this.roleChangeListener.onClientToServer();
            setReceiveListener(new ServerReceiveEvent());
        } else if ((this.getRole() == RoleChangeListener.ROLE_CLIENT || this.getRole() == RoleChangeListener.ROlE_SERVER )&& role == RoleChangeListener.ROLE_WORK) {      // client or server -> work
            this.roleChangeListener.onClientToWork();
            setReceiveListener(new WorkReceiveEvent());
            startRoleDecisionTimer();
            roleChainList.clear();
        }
        this.role = role;
    }

    // 初始化socket
    private void initSocket() {
        try {
            inetAddress = InetAddress.getByName(this.host);
            // 创建组播连接socket
            this.mus = new MulticastSocket(port);
            // 加入组播
            this.mus.joinGroup(inetAddress);
            // 发送加入事件
            sendJoinEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // start 角色决定定时器
    private void startRoleDecisionTimer() {
        workStatusTimer = new Timer();
        //  如果没有server 则由work升级为server
        workStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // work->server
                setRole(RoleChangeListener.ROlE_SERVER);
            }
        }, PERF_TIME);
        setReceiveListener(new WorkReceiveEvent());

    }

    // role初始化
    private void initWorkRole() {
        startRoleDecisionTimer();
        new Thread(new UDPRunnable(this.mus, new top.potens.jnet.broad.listener.EventListener() {
            @Override
            public void onMessage(byte[] bytes) {
                receiveBasicEvent.onMessage(bytes);
            }
        })).start();
    }

    // 统一发送出口
    private void send(byte event, byte[] bytes) {
        try {
            byte[] data = new byte[top.potens.jnet.broad.listener.EventListener.MESSAGE_TOOL_BYTE];
            data[0] = event;
            System.arraycopy(bytes, 0, data, 1, bytes.length);
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, getInetAddress(), getPort());
            getMus().send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送加入事件
    public void sendJoinEvent() {
        send(top.potens.jnet.broad.listener.EventListener.EVENT_W_JOIN, localIpByte);
    }

    // 由server发出的 server的地址
    public void sendServerAddress() {
        send(top.potens.jnet.broad.listener.EventListener.EVENT_S_MANAGE_ADDRESS, localIpByte);
    }

    // 由server发出 角色链
    public void sendRoleChain() {
        StringBuilder sb = new StringBuilder();
        for (String ip : roleChainList) {
            sb.append(ip);
            sb.append(',');
        }
        String ips = sb.toString();
        if (ips.length() > 0) {
            ips = ips.substring(0, ips.length() - 1);
        }
        byte[] bytes = ips.getBytes();
        send(top.potens.jnet.broad.listener.EventListener.EVENT_S_SYNC_ROLE_CHAIN, bytes);
    }

    // 由server发出 有client退出
    public void sendClientExit(String ip) {
        byte[] bytes = ip.getBytes();
        send(top.potens.jnet.broad.listener.EventListener.EVENT_S_CLIENT_EXIT, bytes);
    }

    // 由client发出 连接server断开
    public void sendServerDisconnect() {
        byte[] bytes = new byte[]{1};
        send(top.potens.jnet.broad.listener.EventListener.EVENT_C_SERVER_DISCONNECT, bytes);

    }
    // 由client发出 同意加入
    public void sendConsentJoin() {
        send(top.potens.jnet.broad.listener.EventListener.EVENT_C_CONSENT_JOIN, localIpByte);

    }

    // server 处理client退出 通知clients有退出的事件 然后通知同步角色链
    public void disposeClientExit(String ip) {
        sendClientExit(ip);
        roleChainList.remove(ip);
        sendRoleChain();
    }

    // 通知server断开
    public void voteServerDisconnect() {
        sendServerDisconnect();
    }


}
