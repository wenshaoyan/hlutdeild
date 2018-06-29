package top.potens.jnet.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.potens.jnet.listener.FileCallback;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/5/12.
 * 保存message id 和文件的对应关系
 */
public class FileMapping {
    private static final Logger logger = LoggerFactory.getLogger(FileMapping.class);
    private String dir;
    private Map<Integer, Mapping> mapData = new HashMap<Integer, Mapping>();

    private FileMapping() {
    }

    private static FileMapping fileMapping = null;

    //静态工厂方法
    public static FileMapping getInstance() {
        if (fileMapping == null) {
            fileMapping = new FileMapping();
        }
        return fileMapping;
    }

    // 设置目录
    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    // 打开文件
    public boolean add(int id, String fileTexBody) {
        if (!mapData.containsKey(id)) {
            String[] split = fileTexBody.split("\\?");
            if (split.length != 2) {
                return false;
            }
            long size = Long.parseLong(split[0]);
            Mapping mapping = new Mapping(split[1], size, dir);
            mapData.put(id, mapping);
        }
        return true;
    }


    // 写入数据
    public boolean write(final int id, final long seek, final byte[] data, final FileCallback fileReceiveCallback) {
        if (mapData.containsKey(id)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadWrite(id, seek, data, fileReceiveCallback);
                }
            }).start();
        }
        return false;
    }

    private void threadWrite(int id, long seek, byte[] data, FileCallback fileReceiveCallback) {
        Mapping mapping = mapData.get(id);
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(mapping.localFile, "rw");
            rf.seek(seek);
            rf.write(data);
            mapping.addWriteSize(data.length);
            fileReceiveCallback.process(id, mapping.toolSize, seek);
        } catch (IOException e) {
            logger.error("io:", e);
        } finally {
            try {
                if (rf != null) rf.close();
            } catch (IOException e) {
                logger.error("file close", e);
            }
            // 写入完成
            if (mapping.getToolSize() == mapping.writeSize) {
                fileReceiveCallback.end(id, mapping.toolSize);
            }
        }
    }

    class Mapping {
        private String remoterFile;
        private long toolSize;
        private long writeSize = 0;
        private String localFile;

        public Mapping(String file, long size, String dir) {
            this.remoterFile = file;
            this.toolSize = size;
            // 获取文件名称
            File remoteFile = new File(file);
            if (remoteFile.isFile()) {
                this.localFile = dir + System.getProperty("file.separator") + remoteFile.getName();
            }
        }

        public String getRemoterFile() {
            return remoterFile;
        }

        public long getToolSize() {
            return toolSize;
        }


        // 对addWriteSize累加
        public void addWriteSize(long v) {
            this.writeSize += v;
        }
    }


}
