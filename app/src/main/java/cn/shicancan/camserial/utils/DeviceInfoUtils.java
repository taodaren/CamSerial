package cn.shicancan.camserial.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 获取手机设备信息工具类
 */

public class DeviceInfoUtils {
    public static TelephonyManager mTelephonyManager;

    /**
     * 获取手机品牌
     */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机的 IMEI 号码
     * 需 READ_PHONE_STATE 权限
     */
    public static String getPhoneIMEI(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = mTelephonyManager.getDeviceId();
        if (deviceId == null) {
            return "UnKnow";
        } else {
            return deviceId;
        }
    }

    /**
     * 获取手机的 IMSI 号码
     */
    public static String getPhoneIMSI(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return mTelephonyManager.getSubscriberId();
    }

    /**
     * 获取手机号码，有的可得，有的不可得
     */
    public static String getPhoneNum(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return mTelephonyManager.getLine1Number();
    }

    /**
     * 获取手机运营商
     */
    public static String getPhoneOperator(Context context) {
        mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return mTelephonyManager.getSimOperatorName();
    }
}
