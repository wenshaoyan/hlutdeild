package top.potens.jnet.listener;

import top.potens.jnet.protocol.HBinaryProtocol;

/**
 * Created by wenshao on 2018/6/11.
 * 上传或接收文件回调接口
 */
public interface FileCallback {
    // 开始上传或开始接收
    public void start(int id, String path, long size);
    // 上传或接收的进度
    public void process(int id, long size, long process);
    // 上传或接收结束
    public void end(int id, long size);
}
