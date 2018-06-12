package com.potens.jnet.handler;

import com.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wenshao on 2018/5/6.
 */
public class BossClientHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        String str = "早晨起床，在紧凑的时光中，我们自以为很好地把握住，却不知在匆忙中让自己的神经紧绷，未能在完成程序化的洗刷、吃饭等事项时给自己一个舒心的心态。这其实，何曾不是一种时间的流失……\n" +
                "\n" +
                "坐汽车，挤地铁，我们经常是一事一做，或者是做一些纯属消遣式的，却不知如何让自己的脑子动起来，手脚不能工作，但脑子完全可以工作，可以去构思和思索。一名著名的作家，就是在上班的路上，通过口述录音完成了不少作品的初稿。一些看似已经运用的时间完全可以进行双效运应，只是因为我们不懂，而让时间流失……\n" +
                "\n" +
                "人生苦短，你的时间如何运用，决定了你的价值。生活中，或许应该多一点理性，多一点清醒，莫让自己看起来忙忙碌碌，而其实，却有不少时间，在悄悄地流失……";
//        str = "HELLO, WORLD";
        for (int i = 0; i < 1; i++) {
            //ctx.writeAndFlush(str);
            int id = HBinaryProtocol.randomId();
            //sendText(ctx, str, id);
        }
        for (int i = 0; i < 1; i++) {
            /*int id1 = HBinaryProtocol.randomId();
            String filePath1 = "d:\\build.zip";
            sendFileApply(ctx, new File(filePath1), id1);*/
            int id2 = HBinaryProtocol.randomId();
            String filePath2 = "D:\\迅雷下载\\Ylmf_Ghost_Win7_SP1_x64_2018_0210.iso";
            sendFileApply(ctx, new File(filePath2), id2);


        }
        System.out.println("init end");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client close");
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HBinaryProtocol protocol) throws Exception {

        if (protocol.getType()== HBinaryProtocol.TYPE_FILE_AGREE) {
            String str = new String(protocol.getBody(), HBinaryProtocol.DEFAULT_TEXT_CHARSET);
            final String[] split = str.split("\\?");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendFile(ctx, new File(split[1]), protocol.getId());
                }
            }).start();
        }

    }

    // 发送文本
    private void sendText(ChannelHandlerContext ctx, String str, int id) {
        HBinaryProtocol protocol = new HBinaryProtocol(id, HBinaryProtocol.FLAG_BUSINESS, str, HBinaryProtocol.TYPE_TEXT);
        ctx.writeAndFlush(protocol);
    }

    // 发送文件申请包
    private void sendFileApply(ChannelHandlerContext ctx, File msg, int id) throws Exception {
        boolean exists = msg.exists();
        if (!exists) {
            throw new Exception("not found file "+msg.getAbsolutePath());
        }

        // 发送申请包 带上文件名称和文件大小 中间以? 分割
        HBinaryProtocol applyHBinaryProtocol = new HBinaryProtocol(id, HBinaryProtocol.FLAG_BUSINESS, msg.length()+"?"+msg.getAbsolutePath(), HBinaryProtocol.TYPE_FILE_APPLY);
        ctx.writeAndFlush(applyHBinaryProtocol);

    }

    // 发送文件
    private void sendFile(ChannelHandlerContext ctx, File msg, int id) {

        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(msg, "r");;
            // 文件总的大小
            long tool = rf.length();
            // 当前读取的字节数
            long rfSeek = 0;
            // 实际读取的直接说
            int len = 0;
            byte[] bytes = new byte[HBinaryProtocol.BODY_LENGTH];
            int i=0;
            while ((len = rf.read(bytes, 0, bytes.length)) != -1) {
                HBinaryProtocol hBinaryProtocol = new HBinaryProtocol(id, HBinaryProtocol.FLAG_BUSINESS, HBinaryProtocol.TYPE_FILE, tool, rfSeek, rfSeek += len, bytes);
                ctx.writeAndFlush(hBinaryProtocol);
                System.out.println("rfSeek:"+rfSeek);
                rf.seek(rfSeek);
                i++;
                if (i % 10 == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("suc "+i);
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
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}