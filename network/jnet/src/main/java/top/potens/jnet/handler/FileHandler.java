package top.potens.jnet.handler;

import top.potens.jnet.listener.FileCallback;
import top.potens.jnet.protocol.HBinaryProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by wenshao on 2018/6/12.
 * 文件接收
 */
public class FileHandler extends SimpleChannelInboundHandler<HBinaryProtocol> {
    private static ChannelHandlerContext mCtx;

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        mCtx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBinaryProtocol protocol) throws Exception {

    }

    // 发送文件
    public void sendFile(File file, FileCallback fileCallback) throws FileNotFoundException {
        int id = HBinaryProtocol.randomId();
        sendFileApply(file, id);

    }
    // 发送文件申请包
    private void sendFileApply(File msg, int id) throws FileNotFoundException {
        boolean exists = msg.exists();
        if (!exists) {
            throw new FileNotFoundException("not found file " + msg.getAbsolutePath());
        }
        // 发送申请包 带上文件名称和文件大小 中间以? 分割
        HBinaryProtocol applyHBinaryProtocol = HBinaryProtocol.buildSimpleText(id, msg.length() + "?" + msg.getAbsolutePath(), HBinaryProtocol.RECEIVE_SERVER, null, HBinaryProtocol.TYPE_FILE_APPLY);
        mCtx.writeAndFlush(applyHBinaryProtocol);

    }

    // 发送文件
    private void sendFileContinue(File msg, int id) {
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(msg, "r");
            ;
            // 文件总的大小
            long tool = rf.length();
            // 当前读取的字节数
            long rfSeek = 0;
            // 实际读取的直接说
            int len = 0;
            byte[] bytes = new byte[HBinaryProtocol.BODY_LENGTH];
            int i = 0;
            while ((len = rf.read(bytes, 0, bytes.length)) != -1) {
                HBinaryProtocol hBinaryProtocol = HBinaryProtocol.buildReceiveServerFile(id, bytes, rfSeek, rfSeek += len);
                mCtx.writeAndFlush(hBinaryProtocol);
                System.out.println("rfSeek:" + rfSeek);
                rf.seek(rfSeek);
                i++;
                if (i % 20 == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("suc " + i);
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


}
