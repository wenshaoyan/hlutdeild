package top.potens.jnet.event;
import java.util.EventObject;

/**
 * Created by wenshao on 2018/6/23.
 * 事件类 server通知
 */
public class EventServerInform extends EventObject {
    public EventServerInform(Object source) {
        super(source);
    }
}