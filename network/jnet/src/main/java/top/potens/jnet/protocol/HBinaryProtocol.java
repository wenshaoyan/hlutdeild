package top.potens.jnet.protocol;

import top.potens.jnet.common.TypeConvert;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by wenshao on 2018/5/5.
 */
public class HBinaryProtocol {

    public static final String DEFAULT_TEXT_CHARSET = "UTF-8";

    // linux 系统
    public static final byte SYSTEM_LINUX = 0x1;
    // window 系统
    public static final byte SYSTEM_WINDOW = 0x2;
    // unix 系统
    public static final byte SYSTEM_UNIX = 0x3;
    // android 系统
    public static final byte SYSTEM_ANDROID = 0x4;
    // IOS 系统
    public static final byte SYSTEM_IOS = 0x5;
    // Mac 系统
    public static final byte SYSTEM_MAC = 0x6;
    // 其他
    public static final byte SYSTEM_OTHER = 0x10;

    private static byte CURRENT_SYSTEM = SYSTEM_OTHER;

    // 当前系统
    static {
        Properties props = System.getProperties();
        String systemName = props.getProperty("os.name").toLowerCase();
        String systemWindowRe = ".*window.*";
        if (Pattern.matches(systemWindowRe, systemName)) {    // window
            CURRENT_SYSTEM = SYSTEM_WINDOW;
        }
    }


    // 心跳包
    public static final byte FLAG_HEARTBEAT = 0x10;
    // 超时包
    public static final byte FLAG_TIMEOUT = 0x11;
    // 业务数据包
    public static final byte FLAG_BUSINESS = 0x12;


    // 文本类型
    public static final byte TYPE_TEXT = 0x21;
    // 文件发送申请包  在每次发文件前需要发送该包
    public static final byte TYPE_FILE_APPLY = 0x22;
    // 文件发送应许包  接收到该响应后才开始发送
    public static final byte TYPE_FILE_AGREE = 0x23;
    // 文件类型
    public static final byte TYPE_FILE = 0x24;
    // json 字符串
    public static final byte TYPE_JSON = 0x25;


    // 接受者只有server
    public static final byte RECEIVE_SERVER = 0x30;
    // 特定channelId
    public static final byte RECEIVE_ASSIGN = 0x31;
    // 某个组
    public static final byte RECEIVE_GROUP = 0x32;
    // 所有client
    public static final byte RECEIVE_ALL = 0x33;

    // 每条消息的最大长度 最大为10M
    public static final int MAX_LENGTH = 1024 * 1024 * 20;

    // 每条消息的头的长度 除去消息体(body)的长度
    public static final int HEAD_LENGTH = 36;

    // 变长的大小 默认为0
    public int variableLength = 0;
    // 每条消息的body的最大长度
    public static final int BODY_LENGTH = MAX_LENGTH - HEAD_LENGTH - 5;


    // 消息的唯一id
    private int id;

    // 系统类型
    private byte system;

    // 消息标志
    private byte flag;

    // 传输的类型
    private byte type;

    // 消息总长度
    private long length;

    // 当前消息起始的范围
    private long startRange;
    // 当前消息起始的范围
    private long endRange;
    // 消息体
    private byte[] body;
    // 接收者身份类型 默认接受者为server
    private byte receive = RECEIVE_SERVER;

    // 可变长区域==========================

    // 接收者身份标识 如果receive=RECEIVE_ASSIGN 则receiveId为指定人的channelId  如果receive=RECEIVE_GROUP 则receiveId为指定人的groupId
    // 如果receive=RECEIVE_ALL 则receiveId为null
    private String receiveId;
    // receiveId的byte长度 默认长度为0
    private int receiveIdLength = 0;

    // 每条消息的body的实际长度
    private int bodyRealityLength;

    // 字符串body
    private String textBody;

    public HBinaryProtocol(){};
    /*// 心跳包版本
    public HBinaryProtocol() {
        buildMessage(randomId(), FLAG_HEARTBEAT, TYPE_TEXT, 0, 0, 0,RECEIVE_SERVER, null,new byte[]{});
    }*/

    // 完整版
    /*public HBinaryProtocol(int id, byte flag, byte type, long length, long startRange, long endRange,byte receive, String receiveId,byte[] body) {
        buildMessage(id, flag, type, length, startRange, endRange,receive, receiveId, body);
    }*/
    // 构造心跳包消息
    public HBinaryProtocol buildHeartbeat() {
        HBinaryProtocol hBinaryProtocol = new HBinaryProtocol();
        hBinaryProtocol.buildMessage(randomId(), FLAG_HEARTBEAT, TYPE_TEXT, 0, 0, 0,RECEIVE_SERVER, null,new byte[]{});
        return hBinaryProtocol;
    }


    // 指定receive的文本消息
    private HBinaryProtocol buildText(String stringBody, byte receive, String receiveId) {
        HBinaryProtocol hBinaryProtocol = new HBinaryProtocol();
        byte[] bytes = TypeConvert.stringToBytes(stringBody);
        hBinaryProtocol.buildMessage(randomId(), FLAG_BUSINESS, TYPE_TEXT, bytes.length, 0, bytes.length, receive, receiveId,bytes);
        return hBinaryProtocol;
    }

    // 指定接收人的文本消息
    public HBinaryProtocol buildReceiveAssignText(String stringBody, String channelIdString) {
        return buildText(stringBody, RECEIVE_ASSIGN, channelIdString);
    }
        // 指定接收group的文本消息
    public HBinaryProtocol buildReceiveGroupText(String stringBody, String channelGroup) {
        return buildText(stringBody, RECEIVE_GROUP, channelGroup);
    }
    // 指定只server接收的文本消息
    public HBinaryProtocol buildReceiveServerText(String stringBody) {
        return buildText(stringBody, RECEIVE_SERVER, null);
    }




    // 通用版本
    /*public HBinaryProtocol(int id, byte flag, String stringBody, byte type) {
        byte[] bytes;
        try {
            bytes = stringBody.getBytes(HBinaryProtocol.DEFAULT_TEXT_CHARSET);
            buildMessage(id, flag, type, bytes.length, 0, bytes.length, RECEIVE_SERVER, null, bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }*/

    // 构造Message
    private void buildMessage(int id, byte flag, byte type, long length, long startRange, long endRange,byte receive, String receiveId, byte[] body) {
        this.id = id;
        this.system = CURRENT_SYSTEM;
        this.flag = flag;
        this.type = type;
        this.length = length;
        this.startRange = startRange;
        this.endRange = endRange;
        this.body = body;
        this.receive = receive;
        this.receiveId = receiveId;
        this.receiveIdLength = receiveId == null ? 0 : TypeConvert.stringToBytes(receiveId).length;
        this.bodyRealityLength = BODY_LENGTH - this.receiveIdLength;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getSystem() {
        return system;
    }

    public void setSystem(byte system) {
        this.system = system;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getStartRange() {
        return startRange;
    }

    public void setStartRange(long startRange) {
        this.startRange = startRange;
    }

    public long getEndRange() {
        return endRange;
    }

    public void setEndRange(long endRange) {
        this.endRange = endRange;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte getReceive() {
        return receive;
    }

    public void setReceive(byte receive) {
        this.receive = receive;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public int getReceiveIdLength() {
        return receiveIdLength;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public int getBodyRealityLength() {
        return bodyRealityLength;
    }

    public static int randomId() {
        Random random = new Random();
        int max = Integer.MAX_VALUE;
        int min = 1;
        return random.nextInt(max - min + 1) + min;
    }

    // 测量实际body的长度
    public static int calculationBodyRealityLength(String receiveId) {
        return BODY_LENGTH - TypeConvert.stringToBytes(receiveId).length;
    }
}
