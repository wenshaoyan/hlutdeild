package top.potens.jnet.broad.event;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.broad.listener.BroadEventListener;
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
    private ReceiveBasicBroadEvent receiveBasicEvent;
    private static int PERF_TIME = 1300;        // 性能时间
    private static String localIp;
    private static byte[] localIpByte;
    private String serverIp;

    public String getLocalIp() {
        return localIp;
    }

    public static void setLocalIp(String _localIp) {
        localIpByte= _localIp.getBytes();
        localIp = _localIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    static {
        Properties props = System.getProperties();
        String systemName = props.getProperty("os.name").toLowerCase();
        String systemWindowRe = ".*window.*";
        String systemLinuxRe = ".*linux.*"; // android

        // window +500  android + 1000  linux + 100
        if (Pattern.matches(systemWindowRe, systemName)) {
            PERF_TIME += 500;
        } else if(Pattern.matches(systemLinuxRe, systemName)) {
            PERF_TIME += 1000;
        }
        Random random = new Random();
        int i = random.nextInt(100);
        PERF_TIME += i;
        logger.debug("PERF_TIME = " + PERF_TIME);
    }

    // 当前的角色
    private byte role = RoleChangeListener.ROLE_WORK;

    // work状态的定时器 如果指定时间收到了EVENT_SERVER_ADDRESS则取消定时器 否则work->server
    private Timer workStatusTimer = null;
    // 当前的角色链
    private List<String> roleChainList = new ArrayList<String>();

    private RoleChangeListener roleChangeListener;

    private BroadSocket() {
        logger.debug(getLocalIp());
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


    // 设置回复监听
    private void setReceiveListener(ReceiveBasicBroadEvent e) {
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

            workStatusTimer.cancel();
            setReceiveListener(new ServerReceiveBroadEvent());
            sendServerAddress();
            roleChainList.clear();
            roleChainList.add(localIp);
            this.roleChangeListener.onWorkToServer();
        } else if (this.getRole() == RoleChangeListener.ROLE_WORK && role == RoleChangeListener.ROLE_CLIENT) {    // work -> client
            workStatusTimer.cancel();
            setReceiveListener(new ClientReceiveBroadEvent());
            this.roleChangeListener.onWorkToClient();
        } else if (this.getRole() == RoleChangeListener.ROLE_CLIENT && role == RoleChangeListener.ROlE_SERVER) {    // client -> server
            setReceiveListener(new ServerReceiveBroadEvent());
            this.roleChangeListener.onClientToServer();
        } else if ((this.getRole() == RoleChangeListener.ROLE_CLIENT || this.getRole() == RoleChangeListener.ROlE_SERVER )&& role == RoleChangeListener.ROLE_WORK) {      // client or server -> work
            setReceiveListener(new WorkReceiveBroadEvent());
            startRoleDecisionTimer();
            roleChainList.clear();
            this.roleChangeListener.onClientToWork();
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
            logger.error("init socket error:", e);
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
        setReceiveListener(new WorkReceiveBroadEvent());

    }

    // role初始化
    private void initWorkRole() {
        startRoleDecisionTimer();
        new Thread(new UDPRunnable(this.mus, new BroadEventListener() {
            @Override
            public void onMessage(byte[] bytes) {
                receiveBasicEvent.onMessage(bytes);
            }
        })).start();
    }

    // 统一发送出口
    private void send(byte event, byte[] bytes) {
        try {
            byte[] data = new byte[BroadEventListener.MESSAGE_TOOL_BYTE];
            data[0] = event;
            System.arraycopy(bytes, 0, data, 1, bytes.length);
            DatagramPacket dataPacket = new DatagramPacket(data, data.length, getInetAddress(), getPort());
            getMus().send(dataPacket);
        } catch (IOException e) {
            logger.error("broad send", e);
        }
    }

    // 发送加入事件
    public void sendJoinEvent() {
        send(BroadEventListener.EVENT_W_JOIN, localIpByte);
    }

    // 由server发出的 server的地址
    public void sendServerAddress() {
        send(BroadEventListener.EVENT_S_MANAGE_ADDRESS, localIpByte);
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
        send(BroadEventListener.EVENT_S_SYNC_ROLE_CHAIN, bytes);
    }

    // 由server发出 有client退出
    public void sendClientExit(String ip) {
        byte[] bytes = ip.getBytes();
        send(BroadEventListener.EVENT_S_CLIENT_EXIT, bytes);
    }

    // 由client发出 连接server断开
    public void sendServerDisconnect() {
        byte[] bytes = new byte[]{1};
        send(BroadEventListener.EVENT_C_SERVER_DISCONNECT, bytes);

    }
    // 由client发出 同意加入
    public void sendConsentJoin() {
        send(BroadEventListener.EVENT_C_CONSENT_JOIN, localIpByte);

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
