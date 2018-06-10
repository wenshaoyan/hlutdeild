package com.potens.jnet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * Created by wenshao on 2018/5/6.
 */
public class HBinaryProtocolEncoder extends MessageToByteEncoder<HBinaryProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HBinaryProtocol protocol, ByteBuf out) throws Exception {
        if(null == protocol){
            throw new Exception("msg is null");
        }
        int bodyLength = (int)(protocol.getEndRange()- protocol.getStartRange());
        out.writeInt(HBinaryProtocol.HEAD_LENGTH + bodyLength);
        out.writeInt(protocol.getId());
        out.writeByte(protocol.getSystem());
        out.writeByte(protocol.getFlag());
        out.writeByte(protocol.getType());
        out.writeLong(protocol.getLength());
        out.writeLong(protocol.getStartRange());
        out.writeLong(protocol.getEndRange());
        out.writeBytes(protocol.getBody(), 0, bodyLength);
    }
}
