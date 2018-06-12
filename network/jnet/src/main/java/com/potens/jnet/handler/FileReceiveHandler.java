package com.potens.jnet.handler;

import com.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by wenshao on 2018/6/12.
 * 文件接收
 */
public class FileReceiveHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HBinaryProtocol hBinaryProtocol) throws Exception {

    }
}
