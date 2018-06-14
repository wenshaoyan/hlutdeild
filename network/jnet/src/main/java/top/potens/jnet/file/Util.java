package top.potens.jnet.file;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by wenshao on 2018/6/5.
 * 工具类
 */
public class Util {
    public static final int FILE_PORT = 31416; // 最大接收字节长度

    public static InetAddress getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddress = networkInterface.getInetAddresses(); inetAddress.hasMoreElements(); ) {
                    InetAddress inetAddres = (InetAddress) inetAddress.nextElement();
                    if (!inetAddres.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddres.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddres;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddres;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
