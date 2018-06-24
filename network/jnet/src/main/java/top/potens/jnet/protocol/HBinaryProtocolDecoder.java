package top.potens.jnet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import top.potens.jnet.common.TypeConvert;

import java.util.List;

/**
 * Created by wenshao on 2018/5/6.
 * HBinaryProtocol 解码器
 */
public class HBinaryProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int id = buf.readInt();
        byte system = buf.readByte();
        byte flag = buf.readByte();
        byte type = buf.readByte();
        long length = buf.readLong();
        long startRange = buf.readLong();
        long endRange = buf.readLong();
        byte receive = buf.readByte();
        int receiverIdLength = buf.readInt();
        String receiveId = null;
        if (receiverIdLength > 0) {
            byte[] receiveIdBytes = new byte[receiverIdLength];
            buf.readBytes(receiveIdBytes);
            receiveId = TypeConvert.bytesToString(receiveIdBytes);
        }
        byte[] body = new byte[(int) (endRange - startRange)];
        buf.readBytes(body);
        HBinaryProtocol protocol = HBinaryProtocol.buildFull(id, flag, type, length, startRange, endRange, receive, receiveId, body);
        if (type == HBinaryProtocol.TYPE_TEXT || type == HBinaryProtocol.TYPE_FILE_APPLY || type == HBinaryProtocol.TYPE_FILE_AGREE) {
            protocol.setTextBody(TypeConvert.bytesToString(body));
            protocol.setSystem(system);
        } else if (type == HBinaryProtocol.TYPE_RPC_RES || type == HBinaryProtocol.TYPE_RPC_REQ) {
            protocol.setTextBody(TypeConvert.bytesToString(body));
            protocol.setSystem(system);
        } else if (type == HBinaryProtocol.TYPE_EVENT) {
            protocol.setTextBody(TypeConvert.bytesToString(body));
            protocol.setSystem(system);
        }
        out.add(protocol);

    }
}
