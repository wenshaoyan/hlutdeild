package top.potens.jnet.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshao on 2018/5/12.
 * 保存message id 和文件的对应关系
 */
public class FileMapping {
    private static final String dir = "d:\\tmp";
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

    // 打开文件
    public boolean add(int id, String fileTexBody) {
        if (!mapData.containsKey(id)) {
            String[] split = fileTexBody.split("\\?");
            if (split.length != 2) {
                return false;
            }
            long size = Long.parseLong(split[0]);
            Mapping mapping = new Mapping(split[1], size);
            mapData.put(id, mapping);
        }
        return true;
    }


    // 写入数据
    synchronized public boolean write(int id, long seek, byte[] data) {
        if (mapData.containsKey(id)) {
            Mapping mapping = mapData.get(id);
            //RandomAccessFile rf = null;
            try {
                //rf = new RandomAccessFile(mapping.localFile, "rw");
                RandomAccessFile rf = mapping.getRf();
                rf.seek(seek);
                rf.write(data);
                mapping.addWriteSize(data.length);
                System.out.println("=====写入中"+mapping.writeSize+"=="+mapping.toolSize+ "======="+ mapping.getCount());
            } catch (IOException e) {

            } finally {
                /*try {
                    if (rf != null)rf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                // 写入完成
                if (mapping.getToolSize() == mapping.writeSize) {
                    System.out.println("=====写入完成");
                }
            }
        }
        return false;
    }

    class Mapping {
        private String remoterFile;
        private long toolSize;
        private long writeSize = 0;
        private String localFile;
        private int count = 0;
        private RandomAccessFile rf;

        public Mapping(String file, long size) {
            this.remoterFile = file;
            this.toolSize = size;
            // 获取文件名称
            File remoteFile = new File(file);
            if (remoteFile.isFile()) {
                this.localFile = dir + System.getProperty("file.separator") + remoteFile.getName();
                try {
                    rf = new RandomAccessFile(this.localFile, "rw");
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                }

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
            count++;
            this.writeSize += v;
        }

        public int getCount() {
            return count;
        }

        public RandomAccessFile getRf() {
            return rf;
        }
    }


}
