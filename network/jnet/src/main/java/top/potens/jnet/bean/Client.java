package top.potens.jnet.bean;

/**
 * Created by wenshao on 2018/6/19.
 * 客户端
 */
public class Client {
    // 连接标识id
    private String channelId;
    // 地址
    private String address;
    // 头像
    private String image;
    // 展示名称 如果设置deviceName则获取deviceName 否则获取deviceModel
    private String showName;
    // 设备型号
    private String deviceModel;
    // 设备名称 设备可自定义
    private String deviceName;

    public Client() {
    }

    public Client(String channelId, String address) {
        this.channelId = channelId;
        this.address = address;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        if (showName == null) this.showName = deviceModel;
        this.deviceModel = deviceModel;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        this.showName = deviceName;
    }

    @Override
    public String toString() {
        return "Client{" +
                "channelId='" + channelId + '\'' +
                ", address='" + address + '\'' +
                ", image='" + image + '\'' +
                ", showName='" + showName + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
