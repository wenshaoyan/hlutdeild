package top.potens.jnet.exception;

/**
 * Created by wenshao on 2018/7/8.
 * channel 心跳异常
 */
public class ChannelHeartException extends RuntimeException{
    public ChannelHeartException(String message) {
        super(message);
    }
}
