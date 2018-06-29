package top.potens.teleport.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import top.potens.teleport.activity.IndexActivity;

/**
 * Created by wenshao on 2018/6/29.
 */
public class NetworkUtil {
    private static final Logger logger = LoggerFactory.getLogger(IndexActivity.class);

    // gps获取ip
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("getLocalIpAddress:", e);
        }
        return null;
    }

    // wifi获取ip
    public static String getIp(Context context){
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager)context.getApplicationContext(). getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            assert wifiManager != null;
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        } catch (Exception e) {
            logger.error("getIp:", e);
        }
        return null;
    }

    // 格式化ip地址（192.168.11.1）
    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    // 3G/4g网络IP
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> inetAddresses = intf
                        .getInetAddresses(); inetAddresses.hasMoreElements();) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getIpAddress:", e);
        }
        return null;
    }

    // 获取本机的ip地址（3中方法都包括）
    public static String getLocalIp(Context context){
        String ip = null;
        try {
            ip=getIp(context);
            if (ip==null){
                ip = getIpAddress();
                if (ip==null){
                    ip = getLocalIpAddress();
                }
            }
        } catch (Exception e) {
            logger.error("getLocalIp:", e);
        }
        return ip;
    }

}
