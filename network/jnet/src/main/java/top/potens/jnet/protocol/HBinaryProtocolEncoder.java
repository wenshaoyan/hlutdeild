package top.potens.jnet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.potens.jnet.common.TypeConvert;


/**
 * Created by wenshao on 2018/5/6.
 * HBinaryProtocol 解码器
 */
public class HBinaryProtocolEncoder extends MessageToByteEncoder<HBinaryProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HBinaryProtocol protocol, ByteBuf out) throws Exception {
        if(null == protocol){
            throw new Exception("msg is null");
        }
        int bodyLength = (int)(protocol.getEndRange()- protocol.getStartRange());
        out.writeInt(HBinaryProtocol.HEAD_LENGTH + bodyLength + protocol.getReceiveIdLength());
        out.writeInt(protocol.getId());
        out.writeByte(protocol.getSystem());
        out.writeByte(protocol.getFlag());
        out.writeByte(protocol.getType());
        out.writeLong(protocol.getLength());
        out.writeLong(protocol.getStartRange());
        out.writeLong(protocol.getEndRange());
        out.writeByte(protocol.getReceive());
        out.writeInt(protocol.getReceiveIdLength());
        if (protocol.getReceiveIdLength() > 0 && protocol.getReceiveId()!= null) {
            out.writeBytes(TypeConvert.stringToBytes(protocol.getReceiveId()));
        }
        out.writeBytes(protocol.getBody(), 0, bodyLength);
    }
}
