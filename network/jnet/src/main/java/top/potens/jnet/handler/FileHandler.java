package top.potens.jnet.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.common.FileMapping;
import top.potens.jnet.common.TypeConvert;
import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/6/12.
 * 文件发送和接收
 */
public class FileHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    private static ChannelHandlerContext mCtx;
    private static final Map<Integer, FileCallback> fileMap = new HashMap<>();
    private FileCallback mFileReceiveCallback;


    public FileHandler(FileCallback fileCallback, String fileUpSaveDir) {
        this.mFileReceiveCallback = fileCallback;
        FileMapping.getInstance().setDir(fileUpSaveDir);
    }

    public FileHandler(FileCallback fileCallback) {
        this.mFileReceiveCallback = fileCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        mCtx = ctx;
        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {
        if (protocol.getType() == HBinaryProtocol.TYPE_FILE_AGREE) {
            if (!fileMap.containsKey(protocol.getId())) {
                logger.warn("not found id="+protocol.getId() + " in fileMap");
            } else {
                FileCallback fileCallback = fileMap.get(protocol.getId());
                String[] split = protocol.getTextBody().split("\\?");
                // 通知开始发送
                fileCallback.start(protocol.getId(), split[1], Long.parseLong(split[0]));
                sendFileContinue(new File(split[1]), protocol.getId(), protocol.getReceive(), protocol.getReceiveId());
            }
        }  else if (protocol.getType() == HBinaryProtocol.TYPE_FILE) {
            FileMapping.getInstance().write(protocol.getId(), protocol.getStartRange(), protocol.getBody(), mFileReceiveCallback);
        } else {
            ctx.fireChannelRead(protocol);
        }

    }

    // 发送文件外部调用方法
    public void sendFile(File file, byte receive, String receiveId, FileCallback fileCallback) throws FileNotFoundException {
        int id = HBinaryProtocol.randomId();
        sendFileApply(file, id, receive, receiveId);
        fileMap.put(id, fileCallback);

    }

    // 发送文件申请包
    private void sendFileApply(File file, int id, byte receive, String receiveId) throws FileNotFoundException {
        boolean exists = file.exists();
        if (!exists) {
            throw new FileNotFoundException("not found file " + file.getAbsolutePath());
        }
        // 发送申请包 带上文件名称和文件大小 中间以? 分割
        HBinaryProtocol applyHBinaryProtocol = HBinaryProtocol.buildSimpleText(id, file.length() + "?" + file.getAbsolutePath(), receive, receiveId, HBinaryProtocol.TYPE_FILE_APPLY);
        mCtx.writeAndFlush(applyHBinaryProtocol);

    }

    // 持续发送文件
    private void sendFileContinue(File msg, int id, byte receive, String receiveId) {
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(msg, "r");
            // 文件总的大小
            long tool = rf.length();
            // 当前读取的字节数
            long rfSeek = 0;
            // 实际读取的byte 长度
            int len = 0;
            byte[] bytes;

            if (receiveId == null) {
                bytes = new byte[HBinaryProtocol.BODY_LENGTH];
            } else {
                bytes = new byte[HBinaryProtocol.BODY_LENGTH - TypeConvert.stringToBytes(receiveId).length];
            }
            int i = 0;
            FileCallback fileCallback = fileMap.get(id);
            while ((len = rf.read(bytes, 0, bytes.length)) != -1) {
                HBinaryProtocol hBinaryProtocol = HBinaryProtocol.buildFile(id, bytes, receive, receiveId, rfSeek, rfSeek += len);
                mCtx.writeAndFlush(hBinaryProtocol);
                fileCallback.process(id, tool, rfSeek);
                rf.seek(rfSeek);
                i++;
                if (i % 20 == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("sleep:", e);
                    }
                }
            }
            fileCallback.end(id, tool);
        } catch (IOException e) {
            logger.error("io:", e);
        } finally {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException e) {
                    logger.error("file close", e);
                }
            }
        }
    }



}
