package cn.shicancan.camserial.app;

/**
 * 全局常量
 */

public class AppConstant {
    public static final String TAG_TYC = "tag_tyc";
    public static final String TAG_SERIAL_PORT = "serial_port";
    public static final String TAG_WEB_SOCKET = "web_socket";

    public static final String STATUS_CONNECTED = "Connected";                       // 设备请求连接
    public static final String STATUS_DISCONNECTED = "Disconnected";                 // 设备请求连接断开
    public static final String STATUS_BINDING_SUCCESS = "BindingSuccess";            // 设备与 APP 绑定成功
    public static final String STATUS_BINDING_FAILED = "BindingFailed";              // 设备与 APP 绑定失败
    public static final String STATUS_WAITING = "Waiting";                           // 设备与 APP 绑定等候

    public static final String CMD_DEVICE_INFO = "DeviceInfo";                       // 获取设备信息
    public static final String CMD_DEVICE_STATE = "DeviceState";                     // 获取设备状态
    public static final String CMD_SENSOR_DATA = "SensorData";                       // 获取传感器数据
    public static final String CMD_RECORD_START = "RecordStart";                     // 开始录像
    public static final String CMD_RECORD_STOP = "RecordStop";                       // 停止录像
    public static final String CMD_PUSH_START = "PushStart";                         // 开始推流
    public static final String CMD_PUSH_STOP = "PushStop";                           // 停止推流
    public static final String CMD_FLASH_LIGHT_ON = "ON";                            // 灯光闪烁检测（开）
    public static final String CMD_FLASH_LIGHT_OFF = "OFF";                          // 灯光闪烁检测（关）
    public static final String CMD_FLASH_LIGHT = "FlashLight";                       // 灯光闪烁检测（闪烁）
}
