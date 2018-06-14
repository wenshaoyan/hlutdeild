 package top.potens.jnet.common;

import java.nio.charset.Charset;

/**
 * Created by wenshao on 2018/6/8.
 * java常用类型的bit大小及范围
 * |  type   |  bit   |    min                              |   max                                  |
 * |  char   | 16|8|32|    0                               |   2^8=65535                            |
 * |  short  |  16    |    -2^15=-32768                     |   2^15-1=32767                         |
 * |  int    |  32    |    -2^31=-2147483648                |   2^31-1=2147483647                    |
 * |  long   |  64    |    -2^63=-9223372036854775808       |   2^63-1=9223372036854775807           |
 * |  float  |  32    |    -2^149=1.4E-45                   |   2^128-1=3.4028235E38                 |
 * |  double |  64    |    -2^1074=4.9E-324                 |   2^1024-1=1.7976931348623157E308      |
 * ========================================
 * 8个bit为的最大为255, 二进制表示为1111 1111, 十六进制表示为0xff
 * 比如short 100, 转换成二进制0000 0000 0110 0100
 * ========================================
 * 原码、反码和补码
 * 正数的原码、反码和补码都是一样。
 * 负数: 如：-100
 * 原码：1110 0100  (最高位为符号位 1 负数 0 正数)
 * 反码：1001 1011
 * 补码：1001 1100
 * ===============================
 * java byte保存为补码
 * ================================
 * type & 0xff意义: 去除除了最后8位的其余高位，只获取最后8位。
 * 类型转换
 */
public class TypeConvert {
    public static final int BYTE_LENGTH = 1;
    public static final int CHAR_LENGTH = 2;
    public static final int SHORT_LENGTH = 2;
    public static final int INT_LENGTH = 4;
    public static final int LONG_LENGTH = 8;
    public static final int FLOAT_LENGTH = 4;
    public static final int DOUBLE_LENGTH = 8;
    public static byte[] charToBytes(char data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data >> 8) & 0xff)
        };
    }

    public static char bytesToChar(byte[] bytes) {
        return (char) (bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8);
    }

    public static byte[] shortToBytes(short data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data >> 8) & 0xff)
        };
    }

    public static short bytesToShort(byte[] bytes) {
        return (short) (bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8);
    }

    public static byte[] intToBytes(int data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 24) & 0xff),
        };
    }

    public static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) |
                ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[3] & 0xFF) << 24);
    }

    public static byte[] longToBytes(long data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 24) & 0xff),
                (byte) ((data >> 32) & 0xff),
                (byte) ((data >> 40) & 0xff),
                (byte) ((data >> 48) & 0xff),
                (byte) ((data >> 56) & 0xff),
        };
    }

    public static long bytesToLong(byte[] bytes) {
        return ((long) bytes[0] & 0xFF) |
                ((long) (bytes[1] & 0xFF) << 8) |
                ((long) (bytes[2] & 0xFF) << 16) |
                ((long) (bytes[3] & 0xFF) << 24) |
                ((long) (bytes[4] & 0xFF) << 32) |
                ((long) (bytes[5] & 0xFF) << 40) |
                ((long) (bytes[6] & 0xFF) << 48) |
                ((long) (bytes[7] & 0xFF) << 56);
    }

    public static byte[] floatToBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return intToBytes(intBits);
    }

    public static float bytesToFloat(byte[] bytes) {
        return Float.intBitsToFloat(bytesToInt(bytes));
    }

    public static byte[] doubleToBytes(double data) {
        long longBits = Double.doubleToLongBits(data);
        return longToBytes(longBits);
    }

    public static double bytesToDouble(byte[] bytes) {
        long l = bytesToLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public static byte[] stringToBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }
    public static byte[] stringToBytes(String data) {
        return stringToBytes(data, "UTF-8");
    }
    public static String bytesToString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }
    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, "UTF-8");
    }

}
