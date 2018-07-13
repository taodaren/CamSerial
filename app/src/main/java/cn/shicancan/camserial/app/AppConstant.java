package cn.shicancan.camserial.app;

/**
 * 全局常量
 */

public class AppConstant {
    public static final String TAG_TYC = "tag_tyc";
    public static final String TAG_SERIAL_PORT = "serial_port";
    public static final String TAG_WEB_SOCKET = "web_socket";

    public static final String STATUS_CONNECTED = "Connected";                                     // 设备请求连接
    public static final String STATUS_DISCONNECTED = "Disconnected";                               // 设备请求连接断开
    public static final String STATUS_BINDING_SUCCESS = "BindingSuccess";                          // 设备与 APP 绑定成功
    public static final String STATUS_BINDING_FAILED = "BindingFailed";                            // 设备与 APP 绑定失败
    public static final String STATUS_WAITING = "Waiting";                                         // 设备与 APP 绑定等候

    public static final String CMD_DEVICE_INFO = "DeviceInfo";                                     // 获取设备信息
    public static final String CMD_DEVICE_STATE = "DeviceState";                                   // 获取设备状态
    public static final String CMD_SENSOR_DATA = "SensorData";                                     // 获取传感器数据
    public static final String CMD_RECORD_START = "RecordStart";                                   // 开始录像
    public static final String CMD_RECORD_STOP = "RecordStop";                                     // 停止录像
    public static final String CMD_PUSH_START = "PushStart";                                       // 开始推流
    public static final String CMD_PUSH_STOP = "PushStop";                                         // 停止推流
    public static final String CMD_FLASH_LIGHT_ON = "ON";                                          // 灯光闪烁检测（开）
    public static final String CMD_FLASH_LIGHT_OFF = "OFF";                                        // 灯光闪烁检测（关）
    public static final String CMD_FLASH_LIGHT = "FlashLight";                                     // 灯光闪烁检测（闪烁）
    public static final String CMD_LIMIT = "Limit";                                                // 限制参数控制
    public static final String CMD_SYNC_TIME = "SyncTime";                                         // 时间校准
    public static final String CMD_TAKE = "Take";                                                  // 拍照
    public static final String CMD_PHOTO_ID = "PhotoID";                                           // 上传图片
    public static final String CMD_VIDEO_MODE_RECYCLE = "Recycle";                                 // 录像方式（回收）
    public static final String CMD_VIDEO_MODE_UNIDIRECTIONAL = "Unidirectional";                   // 录像方式（单向）

    public static final String EVENT_APP_BIND_DEVICE = "AppBindDevice";                            // 设备与 APP 绑定
    public static final String EVENT_DEVICE_REQ_CONNECTION = "DeviceReqConnection";                // 设备请求连接
    public static final String EVENT_DEVICE_TWINKLE_TEST = "DeviceTwinkleTest";                    // 灯光闪烁检测
    public static final String EVENT_DEVICE_RECORDING = "ConvenientDevicePictureRecording";        // APP 开始录像
    public static final String EVENT_PUSH_STREAM = "PushStream";                                   // 设备开始推流
    public static final String EVENT_SET_LIMIT = "SetLimit";                                       // 限制参数控制
    public static final String EVENT_GET_SENSOR_DATA = "GetSensorData";                            // 获取传感器数据
    public static final String EVENT_GET_DEVICE_INFO = "GetDeviceInfo";                            // 获取设备信息
    public static final String EVENT_GET_DEVICE_STATE = "GetDeviceState";                          // 获取设备状态
    public static final String EVENT_SET_TIME = "SetTime";                                         // 时间校准
    public static final String EVENT_TAKE_PHOTO = "TakePhoto";                                     // 拍照
    public static final String EVENT_UPLOAD_PHOTO = "UploadPhoto";                                 // 上传图片
    public static final String EVENT_RECORD_MODE = "RecordMode";                                   // 录像方式

    public static final String VALUE_SUCCESS = "Success";                                          // 成功
    public static final String VALUE_FAILED = "Failed";                                            // 失败
    public static final String VALUE_DEV = "Dev";                                                  // Dev
    public static final String VALUE_CONNECT = "Connect";                                          // 接通
    public static final String VALUE_DISCONNECT = "Disconnect";                                    // 断开
}
