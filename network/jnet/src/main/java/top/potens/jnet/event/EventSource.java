package top.potens.jnet.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by wenshao on 2018/6/23.
 * 事件源类。表明谁触发了事件，用于作为EventObject类的构造参数，在listener中作路由
 */

public class EventSource {
    private static final Logger logger = LoggerFactory.getLogger(EventSource.class);

    private Vector<EventListener> listeners;
    public EventSource() {
        this.listeners = new Vector<>();

    }
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    public int size() {
        return listeners.size();
    }
    public void clear() {
        listeners.clear();
    }

    public void message(String method, String args) {
        for (EventListener listener : listeners){
            Class<? extends EventListener> aClass = listener.getClass();
            try {
                Method m = aClass.getMethod(method, EventObject.class, String.class);
                m.invoke(listener, new EventServerInform(this), args);
            } catch (Exception e) {
                logger.error("invoke error:", e);
            }
        }

    }
}
