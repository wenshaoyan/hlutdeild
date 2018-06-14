package top.potens.jnet.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;

/**
 * Created by wenshao on 2018/6/5.
 * 文件发送方
 */
public class SendFile {

    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private int port;
    private File file;
    private int id;


    public SendFile(int id, File file, InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.id = id;
        this.file = file;
        this.port = port;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void send() {


        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(file, "r");
            // 当前读取的字节数
            long rfSeek = 0;
            // 实际读取的长度
            int len = 0;
            int count = 0;
            byte[] bytes = new byte[Message.BODY_LENGTH];
            while ((len = rf.read(bytes, 0, bytes.length)) != -1) {
                Message message = new Message(this.getId(), Message.TYPE_UP, rfSeek, len, bytes);
                DatagramPacket datagramPacket = new DatagramPacket(message.toByte(), message.getToolSize(), this.inetAddress, this.port);
                if (count % 20 == 0) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                this.datagramSocket.send(datagramPacket);
                rfSeek += len;
                rf.seek(rfSeek);
                count++;
            }
            System.out.println(rfSeek);
            System.out.println("suc " + count);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//            byte[] buf = sendStr.getBytes();
//            datagramPacket = new DatagramPacket(buf, buf.length, this.inetAddress, this.port);
        // 发送数据
        //datagramSocket.send(datagramPacket);


    }

    public static void main(String[] args) {
        //String filePath2 = "D:\\source.txt";
        //new SendFile(1, new File(filePath2), Util.getLocalHostLANAddress(), Util.FILE_PORT).send();

        String filePath1 = "D:\\download\\os\\CentOS-7-x86_64-DVD-1708.iso";
        new SendFile(2, new File(filePath1), Util.getLocalHostLANAddress(), Util.FILE_PORT).send();
        //String filePath3 = "D:\\data\\build.zip";
        //SendFile s = new SendFile(3, new File(filePath3), Util.getLocalHostLANAddress(), Util.FILE_PORT);
        //s.send();

    }
}
