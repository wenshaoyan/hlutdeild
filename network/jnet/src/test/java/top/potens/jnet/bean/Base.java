package top.potens.jnet.bean;

/**
 * Created by wenshao on 2018/6/14.
 * 发送JSON和接受JSON的基类
 */

public class Base<T> {
    private static final byte RECEIVE_ASSIGN = 0x01;   // 特定channelId
    private static final byte RECEIVE_GROUP = 0x02;    // 某个组
    private static final byte RECEIVE_ALL = 0x03;      // 所有client


    private byte receive;
    private String channelId;
    private String groupId;
    private byte[] data;

    public Base(String channelId, String groupId, byte[] data) {


    }

    public byte getReceive() {
        return receive;
    }

    public void setReceive(byte receive) {
        this.receive = receive;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
