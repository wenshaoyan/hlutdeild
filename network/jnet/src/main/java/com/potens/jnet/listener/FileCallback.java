package com.potens.jnet.listener;

import com.potens.jnet.protocol.HBinaryProtocol;

/**
 * Created by wenshao on 2018/6/11.
 * 上传或下载文件回调接口
 */
public interface FileCallback {
    // 上传的请求申请
    public void apply(HBinaryProtocol hBinaryProtocol, int id, String path, long size);
    // 上传的进度
    public void process(HBinaryProtocol hBinaryProtocol, int id, long size, int process);
    // 上传结束
    public void end(HBinaryProtocol hBinaryProtocol, int id, long size);
}
