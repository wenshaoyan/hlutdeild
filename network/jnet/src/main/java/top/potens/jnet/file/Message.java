package top.potens.jnet.file;


import top.potens.jnet.common.TypeConvert;

import java.util.Random;


/**
 * Created by wenshao on 2018/6/5.
 * 发送文件时候的message
 */
public class Message {
    // 最大接收字节长度
    public static final int MAX_LENGTH = 1024 * 60;

    // 下载
    public final static byte TYPE_DOWN = 0x1;
    // 上传
    public final static byte TYPE_UP = 0x2;

    public final static int HEAD_LENGTH = TypeConvert.BYTE_LENGTH + TypeConvert.LONG_LENGTH + TypeConvert.INT_LENGTH * 2;
    public final static int BODY_LENGTH = MAX_LENGTH - HEAD_LENGTH;


    // 类型
    private byte type;
    // 偏移
    private long seek;
    // 长度
    private int length;
    // id
    private int id;
    // 实际数据
    private byte[] body;
    // 该消息的总大小
    private int toolSize;

    public Message(int id, byte type, long seek, int length, byte[] body) {
        this.id = id;
        this.type = type;
        this.seek = seek;
        this.length = length;
        this.body = body;
        this.toolSize = HEAD_LENGTH + length;
    }
    public Message() {

    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getSeek() {
        return seek;
    }

    public void setSeek(long seek) {
        this.seek = seek;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getToolSize() {
        return toolSize;
    }

    public void setToolSize(int toolSize) {
        this.toolSize = toolSize;
    }

    public static int randomId() {
        Random random = new Random();
        int max = Integer.MAX_VALUE;
        int min = 1;
        return random.nextInt(max - min + 1) + min;
    }
    // message -> byte[]
    public byte[] toByte() {
        byte[] bytes = new byte[this.length + HEAD_LENGTH];
        byte[] ids = TypeConvert.intToBytes(this.getId());
        byte type = this.getType();
        byte[] seeks = TypeConvert.longToBytes(this.getSeek());
        byte[] lens = TypeConvert.intToBytes(this.getLength());
        byte[] body = this.getBody();

        System.arraycopy(ids, 0, bytes, 0, TypeConvert.INT_LENGTH);
        bytes[TypeConvert.INT_LENGTH] = type;

        System.arraycopy(seeks, 0, bytes, TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH, TypeConvert.LONG_LENGTH);
        System.arraycopy(lens, 0, bytes, TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH + TypeConvert.LONG_LENGTH,
                TypeConvert.INT_LENGTH);
        System.arraycopy(body, 0, bytes, TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH + TypeConvert.LONG_LENGTH +
                TypeConvert.INT_LENGTH, this.getLength());
        return bytes;
    }
    // byte[] -> Message
    public static Message toMessage(byte[] bytes) {
        Message message = new Message();
        byte[] ids = new byte[TypeConvert.INT_LENGTH];
        message.setType(bytes[TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH]);
        byte[] seeks = new byte[TypeConvert.LONG_LENGTH];
        byte[] lengths = new byte[TypeConvert.INT_LENGTH];


        System.arraycopy(bytes, 0, ids, 0, ids.length);
        System.arraycopy(bytes, TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH, seeks, 0, seeks.length);
        System.arraycopy(bytes, TypeConvert.INT_LENGTH + TypeConvert.BYTE_LENGTH + TypeConvert.LONG_LENGTH, lengths, 0, lengths.length);


        message.setId(TypeConvert.bytesToInt(ids));
        message.setSeek(TypeConvert.bytesToLong(seeks));
        message.setLength(TypeConvert.bytesToInt(lengths));

        byte[] body = new byte[message.getLength()];
        System.arraycopy(bytes, Message.HEAD_LENGTH - 1, body, 0, body.length);
        message.setBody(body);
        return message;



    }

}
