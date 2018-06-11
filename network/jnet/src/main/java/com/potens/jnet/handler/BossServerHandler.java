package com.potens.jnet.handler;

import com.potens.jnet.common.FileMapping;
import com.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by wenshao on 2018/5/6.
 *
 */
public class BossServerHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {

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
