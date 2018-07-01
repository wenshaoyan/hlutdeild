package top.potens.teleport.util;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;

/**
 * Created by wenshao on 2018/6/30.
 * 获取设备相关的信息
 */
public class DeviceUtil {
    /**
     * 获取设备型号
     * @return      型号
     */
    public static String getDeviceModel(){
        return  Build.MODEL;
    }
    /**
     * 获取设备设置的名称
     * @return      型号
     */
    public static String getDeviceName(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getName();
    }
}
