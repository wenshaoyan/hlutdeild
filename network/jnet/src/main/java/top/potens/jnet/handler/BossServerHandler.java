package top.potens.jnet.handler;

import top.potens.jnet.common.FileMapping;
import top.potens.jnet.helper.ChannelGroupHelper;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by wenshao on 2018/5/6.
 *
 */
public class BossServerHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        /*if (ChannelGroupHelper.size() > 0) {
            HBinaryProtocol hBinaryProtocol = new HBinaryProtocol(HBinaryProtocol.randomId(), HBinaryProtocol.FLAG_BUSINESS, "111", HBinaryProtocol.TYPE_TEXT);
            ChannelGroupHelper.broadcast(hBinaryProtocol);
        }*/
        System.out.println(ch.localAddress());
        System.out.println(ch.remoteAddress());
        System.out.println(ch.id());

        ChannelGroupHelper.add(ch);
        // System.out.println(ChannelGroupHelper.size());
    }
    // channel 断开后的事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();



        System.out.println("====================");
    }

    /**
     * 功能：读取完毕发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // System.out.println("接收数据完毕..");
        // ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol hBinaryProtocol) throws Exception {
        try {
            if (hBinaryProtocol.getType() == HBinaryProtocol.TYPE_TEXT) {
                System.out.println(hBinaryProtocol.getTextBody());
                System.out.println(hBinaryProtocol.getBody().length);
            }
        }finally {
            ReferenceCountUtil.release(hBinaryProtocol);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 发送文件应许包
    private void sendFileAgree(ChannelHandlerContext ctx, HBinaryProtocol hHBinaryProtocol) throws Exception {
        // 发送应许包 带上文件名称和文件大小 中间以? 分割
        HBinaryProtocol applyHBinaryProtocol = new HBinaryProtocol(hHBinaryProtocol.getId(), HBinaryProtocol.FLAG_BUSINESS, hHBinaryProtocol.getTextBody(), HBinaryProtocol.TYPE_FILE_AGREE);
        ctx.writeAndFlush(applyHBinaryProtocol);
        FileMapping.getInstance().add(hHBinaryProtocol.getId(), hHBinaryProtocol.getTextBody());
    }
}
