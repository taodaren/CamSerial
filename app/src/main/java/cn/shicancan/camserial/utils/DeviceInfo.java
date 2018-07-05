package cn.shicancan.camserial.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 获取手机设备信息
 */

public class DeviceInfo {

    /**
     * 获取设备宽度（px）
     */
    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取设备 IMEI 号码（唯一标识）
     * 需  <uses-permission android:name="android.permission.READ_PHONE_STATE" /> 权限
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            return "UnKnow";
        } else {
            return deviceId;
        }
    }

    /**
     * 获取厂商名
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取产品名
     */
    public static String getDeviceProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机主板名
     */
    public static String getDeviceBoard() {
        return Build.BOARD;
    }

    /**
     * 获取设备名
     */
    public static String getDeviceDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取手机硬件序列号
     */
    public static String getDeviceSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取手机 Android 系统 SDK
     */
    public static int getDeviceSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机 Android 版本
     */
    public static String getDeviceAndroidBersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言
     */
    public static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表（Locale 列表）
     */
    public static Locale[] getDeviceSupportLanguage() {
        return Locale.getAvailableLocales();
    }

    /**
     * 判断 SD 是否挂载方法
     */
    public static boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

//    /**
//     * 获取 SD 存储信息的方法
//     * @param context
//     * @param type 用于区分内置存储于外置存储的方法
//     *             内置SD卡：INTERNAL_STORAGE = 0;
//     *             外置SD卡：EXTERNAL_STORAGE = 1;
//     * @return
//     */
//    public static String getStorageInfo(Context context, int type) {
//        String path = getStoragePath(context, type);
//        if (isSDCardMount() == false || TextUtils.isEmpty(path) || path == null) {
//            return "无外置"
//        }
//    }
//
//    /**
//     * 使用反射方法，获取手机存储路径
//     */
//    public static String getStoragePath(Context context, int type) {
//        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        try {
//            Method getPathsMethod = sm.getClass().getMethod("getVolumePaths", null);
//            String[] path = (String[]) getPathsMethod.invoke(sm, null);
//            switch (type) {
//                case INTERNAL_STORAGE:
//                    return path[type];
//                case EXTERNAL_STORAGE:
//                    if (path.length > 1) {
//                        return path[type];
//                    } else {
//                        return null;
//                    }
//                default:
//                    break;
//            }
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
