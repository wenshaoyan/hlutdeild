package top.potens.jnet.handler;

import top.potens.jnet.protocol.HBinaryProtocol;
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
                "人生苦短，你的时间如何运用，决定了你的价值。生活中，或许应该多一点理性，多一点清醒，莫让自己看起来忙忙碌碌，而其实，却有不少时间，在悄悄地流失……1";
//        str = "HELLO, WORLD";
        for (int i = 0; i < 1; i++) {
            //ctx.writeAndFlush(str);
            //int id = HBinaryProtocol.randomId();
            sendText(ctx, str);
        }
        for (int i = 0; i < 1; i++) {
            /*int id1 = HBinaryProtocol.randomId();
            String filePath1 = "d:\\build.zip";
            sendFileApply(ctx, new File(filePath1), id1);*/
            int id2 = HBinaryProtocol.randomId();
            String filePath2 = "D:\\迅雷下载\\Ylmf_Ghost_Win7_SP1_x64_2018_0210.iso";
            // sendFileApply(ctx, new File(filePath2), id2);

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
//            System.out.println("=======================");
//
//            String str = new String(protocol.getBody(), HBinaryProtocol.DEFAULT_TEXT_CHARSET);
//            final String[] split = str.split("\\?");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    sendFile(ctx, new File(split[1]), protocol.getId());
//                }
//            }).start();
        } else if (protocol.getType()== HBinaryProtocol.TYPE_TEXT) {
            System.out.println("接收到消息:"+protocol.getTextBody());
        }

    }

    // 发送文本
    private void sendText(ChannelHandlerContext ctx, String str) {
        HBinaryProtocol protocol = HBinaryProtocol.buildReceiveServerText(str);
        ctx.writeAndFlush(protocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}