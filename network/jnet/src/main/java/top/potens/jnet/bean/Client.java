package top.potens.jnet.bean;

import java.net.InetAddress;

/**
 * Created by wenshao on 2018/6/19.
 * 客户端
 */
public class Client {
    // 连接标识id
    private String channelId;
    // 地址
    private InetAddress address;
    // 头像
    private String image;
    // MAC地址
    private String mac;
    // 设备名称
    private String device;


    public Client(String channelId, InetAddress address, String image, String mac, String device) {
        this.channelId = channelId;
        this.address = address;
        this.image = image;
        this.mac = mac;
        this.device = device;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "Client{" +
                "channelId='" + channelId + '\'' +
                ", address=" + address +
                ", image='" + image + '\'' +
                ", mac='" + mac + '\'' +
                ", device='" + device + '\'' +
                '}';
    }
}
