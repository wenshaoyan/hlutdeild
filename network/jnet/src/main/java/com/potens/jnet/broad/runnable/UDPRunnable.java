package com.potens.jnet.broad.runnable;

import com.potens.jnet.broad.listener.EventListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Created by wenshao on 2018/5/23.
 * 监听组播事件的线程
 */
public class UDPRunnable implements Runnable {
    private MulticastSocket mus;
    private EventListener listener;

    public UDPRunnable(MulticastSocket mus, EventListener listener) {
        this.mus = mus;
        this.listener = listener;
    }

    public void run() {
        byte buf[] = new byte[EventListener.MESSAGE_TOOL_BYTE];
        DatagramPacket dp = new DatagramPacket(buf, EventListener.MESSAGE_TOOL_BYTE);
        while (true) {
            // 阻塞该线程 接受组播的数据
            try {
                mus.receive(dp);
                listener.onMessage(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}