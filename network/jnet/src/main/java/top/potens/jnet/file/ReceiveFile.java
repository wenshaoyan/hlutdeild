package top.potens.jnet.file;

import java.io.*;
import java.net.*;

/**
 * Created by wenshao on 2018/6/5.
 * 文件接收方
 */
public class ReceiveFile {

    // 数据报套接字
    private DatagramSocket datagramSocket;

    public ReceiveFile() {
        try {
            //接收数据流程
            // 创建一个数据报套接字，并将其绑定到指定port上
            datagramSocket = new DatagramSocket(Util.FILE_PORT);
            receiver();
            // wait();
        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            // 关闭socket
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }


    }

    public void receiver() {
        int i=0;
        while (true) {
            // 阻塞该线程 接受组播的数据
            try {
                byte[] buf = new byte[Message.MAX_LENGTH];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(dp);
                System.out.println(i++);
                /*byte[] bytes = dp.getData();
                Message message = Message.toMessage(bytes);
                FileMapping.getInstance().write(message.getId(), message.getSeek(), message.getBody());
                System.out.println("Server receive:" + message.getId());
                System.out.println("Server Port:" + dp.getPort());*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        FileMapping.getInstance().add(1, "20?D:\\source.txt");
        FileMapping.getInstance().add(2, "20?D:\\download\\os\\CentOS-7-x86_64-DVD-1708.iso");
        FileMapping.getInstance().add(3, "394187443?D:\\data\\build.zip");
        new ReceiveFile();
    }

}
