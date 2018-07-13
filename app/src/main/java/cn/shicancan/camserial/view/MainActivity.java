package cn.shicancan.camserial.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;

import cn.shicancan.camserial.R;
import cn.shicancan.camserial.model.ReceiveCmdBean;
import cn.shicancan.camserial.model.ReceiveStatusBean;
import cn.shicancan.camserial.model.SendDeviceBindAppBean;
import cn.shicancan.camserial.model.SendDeviceRequestConnBean;
import cn.shicancan.camserial.model.SendGetDeviceInfoBean;
import cn.shicancan.camserial.model.SendGetDeviceStatusBean;
import cn.shicancan.camserial.model.SendGetSensorInfoBean;
import cn.shicancan.camserial.model.SendHeartLinkBean;
import cn.shicancan.camserial.model.SendLightFlashBean;
import cn.shicancan.camserial.model.SendParameterControlBean;
import cn.shicancan.camserial.model.SendPhotographBean;
import cn.shicancan.camserial.model.SendPushStreamBean;
import cn.shicancan.camserial.model.SendRecordStartBean;
import cn.shicancan.camserial.model.SendTimeCalibrationBean;
import cn.shicancan.camserial.model.SendUploadImgBean;
import cn.shicancan.camserial.model.SendVideoModeBean;
import cn.shicancan.camserial.presenter.IPushVideoAidlInterface;
import cn.shicancan.camserial.presenter.Urls;

import static cn.shicancan.camserial.app.AppConstant.CMD_DEVICE_INFO;
import static cn.shicancan.camserial.app.AppConstant.CMD_DEVICE_STATE;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT_OFF;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT_ON;
import static cn.shicancan.camserial.app.AppConstant.CMD_LIMIT;
import static cn.shicancan.camserial.app.AppConstant.CMD_PHOTO_ID;
import static cn.shicancan.camserial.app.AppConstant.CMD_PUSH_START;
import static cn.shicancan.camserial.app.AppConstant.CMD_PUSH_STOP;
import static cn.shicancan.camserial.app.AppConstant.CMD_RECORD_START;
import static cn.shicancan.camserial.app.AppConstant.CMD_RECORD_STOP;
import static cn.shicancan.camserial.app.AppConstant.CMD_SENSOR_DATA;
import static cn.shicancan.camserial.app.AppConstant.CMD_SYNC_TIME;
import static cn.shicancan.camserial.app.AppConstant.CMD_TAKE;
import static cn.shicancan.camserial.app.AppConstant.CMD_VIDEO_MODE_RECYCLE;
import static cn.shicancan.camserial.app.AppConstant.CMD_VIDEO_MODE_UNIDIRECTIONAL;
import static cn.shicancan.camserial.app.AppConstant.EVENT_APP_BIND_DEVICE;
import static cn.shicancan.camserial.app.AppConstant.EVENT_DEVICE_RECORDING;
import static cn.shicancan.camserial.app.AppConstant.EVENT_DEVICE_REQ_CONNECTION;
import static cn.shicancan.camserial.app.AppConstant.EVENT_DEVICE_TWINKLE_TEST;
import static cn.shicancan.camserial.app.AppConstant.EVENT_GET_DEVICE_INFO;
import static cn.shicancan.camserial.app.AppConstant.EVENT_GET_DEVICE_STATE;
import static cn.shicancan.camserial.app.AppConstant.EVENT_GET_SENSOR_DATA;
import static cn.shicancan.camserial.app.AppConstant.EVENT_PUSH_STREAM;
import static cn.shicancan.camserial.app.AppConstant.EVENT_RECORD_MODE;
import static cn.shicancan.camserial.app.AppConstant.EVENT_SET_LIMIT;
import static cn.shicancan.camserial.app.AppConstant.EVENT_SET_TIME;
import static cn.shicancan.camserial.app.AppConstant.EVENT_TAKE_PHOTO;
import static cn.shicancan.camserial.app.AppConstant.EVENT_UPLOAD_PHOTO;
import static cn.shicancan.camserial.app.AppConstant.STATUS_BINDING_FAILED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_BINDING_SUCCESS;
import static cn.shicancan.camserial.app.AppConstant.STATUS_CONNECTED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_DISCONNECTED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_WAITING;
import static cn.shicancan.camserial.app.AppConstant.TAG_SERIAL_PORT;
import static cn.shicancan.camserial.app.AppConstant.TAG_WEB_SOCKET;
import static cn.shicancan.camserial.app.AppConstant.VALUE_CONNECT;
import static cn.shicancan.camserial.app.AppConstant.VALUE_DEV;
import static cn.shicancan.camserial.app.AppConstant.VALUE_DISCONNECT;
import static cn.shicancan.camserial.app.AppConstant.VALUE_SUCCESS;
import static cn.shicancan.camserial.utils.DeviceInfoUtils.getPhoneBrand;
import static cn.shicancan.camserial.utils.DeviceInfoUtils.getPhoneIMEI;
import static cn.shicancan.camserial.utils.DeviceInfoUtils.getPhoneModel;
import static cn.shicancan.camserial.utils.DeviceInfoUtils.getPhoneNum;
import static cn.shicancan.camserial.utils.DeviceInfoUtils.getPhoneOperator;
import static cn.shicancan.camserial.utils.MacAddressUtils.getConnectedWifiMacAddress;
import static cn.shicancan.camserial.utils.MacAddressUtils.getMacAddress;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getAvailMemory;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getCpuName;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getCurCpuFreq;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getMaxCpuFreq;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getMinCpuFreq;
import static cn.shicancan.camserial.utils.MemoryCpuUtils.getTotalMemory;

public class MainActivity extends Activity {
    private String mModel, mDeviceId, mDeviceNum, mMacWifi, mMacPhone;
    private AsyncHttpClient mClient;
    private Gson mGson;
    // 推流接口
    private IPushVideoAidlInterface mIPush;
    // 定义推流
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIPush = IPushVideoAidlInterface.Stub.asInterface(iBinder);
            // IPushVideoAidlInterface 已经得到，想干什么干什么
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    // 串口相关
    protected SerialPort mSerialPort;
    private ReadThread mReadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGson = new Gson();
        mClient = AsyncHttpClient.getDefaultInstance();

        getPhoneInfo();
        setWebSocket();
        bindVideoStream();
    }

    /**
     * 获取手机信息
     */
    @SuppressLint("SetTextI18n")
    private void getPhoneInfo() {
        TextView tvPhoneInfo = findViewById(R.id.tv_phone_info);

        mModel = getPhoneModel();
        mDeviceId = getPhoneIMEI(this);
        mDeviceNum = "10008";
        mMacWifi = getConnectedWifiMacAddress(this);
        mMacPhone = getMacAddress(this);

        tvPhoneInfo.setText("品牌：" + getPhoneBrand() + "\n");
        tvPhoneInfo.append("型号：" + mModel + "\n");
        tvPhoneInfo.append("版本：Android " + android.os.Build.VERSION.RELEASE + "\n\n");
        tvPhoneInfo.append("手机号：" + getPhoneNum(this) + "\n");
        tvPhoneInfo.append("运营商：" + getPhoneOperator(this) + "\n\n");
        tvPhoneInfo.append("IMEI：" + mDeviceId + "\n");
//        tvPhoneInfo.append("IMSI：" + getPhoneIMSI(this) + "\n");
        tvPhoneInfo.append("MAC_WIFI：" + mMacWifi + "\n");
        tvPhoneInfo.append("MAC_PHONE：" + mMacPhone + "\n\n");
        tvPhoneInfo.append("总内存：" + getTotalMemory(this) + "\n");
        tvPhoneInfo.append("当前可用内存：" + getAvailMemory(this) + "\n\n");
        tvPhoneInfo.append("CPU 名字：" + getCpuName() + "\n");
        tvPhoneInfo.append("CPU 最大频率：" + getMaxCpuFreq() + "\n");
        tvPhoneInfo.append("CPU 最小频率：" + getMinCpuFreq() + "\n");
        tvPhoneInfo.append("CPU 当前频率：" + getCurCpuFreq() + "\n");
    }

    /**
     * 通过 WebSocket 实现长连接通讯消息
     */
    private void setWebSocket() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT,
                new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送开机心跳数据给后台
                        sendHeartLinkInfo(webSocket);

                        // 接收后台数据后的操作，该方法是个线程，如果要修改页面，记得放在主线程里
                        webSocket.setStringCallback(new WebSocket.StringCallback() {
                            @Override
                            public void onStringAvailable(String jsonSocket) {
                                Log.e(TAG_WEB_SOCKET, "来自于服务器的长连接信息--->" + jsonSocket);
                                switch (jsonSocket.substring(2, 5)) {
                                    case "Sta":
                                        // 处理服务器状态
                                        handleServerStatus(jsonSocket);
                                        break;
                                    case "Cmd":
                                        // 处理服务器命令
                                        handleServerCmd(jsonSocket);
                                        break;
                                    default:
                                        // 正常接收心跳包 @heartLink
                                        break;
                                }
                            }
                        });

                        webSocket.setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                Log.i(TAG_WEB_SOCKET, "可用的数据: 我得到了一些字节!");
                                // 请注意，此数据已被读取
                                bb.recycle();
                            }
                        });
                    }
                }
        );
    }

    /**
     * 绑定视频推流服务
     */
    private void bindVideoStream() {
        Intent intent = new Intent("cn.shicancan.uvcdirectcamera.IPushVideoAidlInterface");
        intent.setPackage("cn.shicancan.uvcdirectcamera");
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    // TODO: 以下 handle() 皆是处理服务器发送数据相关方法

    private void handleServerStatus(String jsonSocket) {
        ReceiveStatusBean bean = mGson.fromJson(jsonSocket, ReceiveStatusBean.class);
        switch (bean.getStatus()) {
            case STATUS_CONNECTED:
                // 设备请求连接（连接）
                if (bean.getEvent().equals(EVENT_DEVICE_REQ_CONNECTION)) {
                    sendDeviceReqConn();
                }
                break;
            case STATUS_DISCONNECTED:
                // 设备请求连接（断开）
                if (bean.getEvent().equals(EVENT_DEVICE_REQ_CONNECTION)) {
                    sendDeviceReqDisconn();
                }
                break;
            case STATUS_BINDING_SUCCESS:
            case STATUS_BINDING_FAILED:
            case STATUS_WAITING:
                // 设备与 APP 绑定
                if (bean.getEvent().equals(EVENT_APP_BIND_DEVICE)) {
                    sendAppBindDevice();
                }
                break;
            default:
                break;
        }
    }

    private void handleServerCmd(String jsonSocket) {
        ReceiveCmdBean bean = mGson.fromJson(jsonSocket, ReceiveCmdBean.class);
        switch (bean.getCmd()) {
            case CMD_PUSH_START:
                // 开始推流
                if (bean.getEvent().equals(EVENT_PUSH_STREAM)) {
                    sendPushStream();
                }
                break;
            case CMD_PUSH_STOP:
                // 停止推流
                if (bean.getEvent().equals(EVENT_PUSH_STREAM)) {
                    sendStopStream();
                }
                break;
            case CMD_FLASH_LIGHT:
                // 灯光闪烁检测（闪烁）
                if (bean.getEvent().equals(EVENT_DEVICE_TWINKLE_TEST)) {
                    sendFlashLight();
                }
                break;
            case CMD_FLASH_LIGHT_ON:
                // 灯光闪烁检测（开）
                if (bean.getEvent().equals(EVENT_DEVICE_TWINKLE_TEST)) {
                    sendFlashLightOn();
                }
                break;
            case CMD_FLASH_LIGHT_OFF:
                // 灯光闪烁检测（关）
                if (bean.getEvent().equals(EVENT_DEVICE_TWINKLE_TEST)) {
                    sendFlashLightOff();
                }
                break;
            case CMD_DEVICE_INFO:
                // 获取设备信息
                if (bean.getEvent().equals(EVENT_GET_DEVICE_INFO)) {
                    sendDeviceInfo();
                }
                break;
            case CMD_DEVICE_STATE:
                // 获取设备状态
                if (bean.getEvent().equals(EVENT_GET_DEVICE_STATE)) {
                    sendDeviceState();
                }
                break;
            case CMD_SENSOR_DATA:
                // 获取传感器数据
                if (bean.getEvent().equals(EVENT_GET_SENSOR_DATA)) {
                    sendSensorData();
                }
                break;
            case CMD_RECORD_START:
                // 开始录像
                if (bean.getEvent().equals(EVENT_DEVICE_RECORDING)) {
                    sendRecordStart();
                }
                break;
            case CMD_RECORD_STOP:
                // 停止录像
                if (bean.getEvent().equals(EVENT_DEVICE_RECORDING)) {
                    sendRecordStop();
                }
                break;
            case CMD_LIMIT:
                // 限制参数控制
                if (bean.getEvent().equals(EVENT_SET_LIMIT)) {
                    sendSetLimit();
                }
                break;
            case CMD_SYNC_TIME:
                // 时间校准
                if (bean.getEvent().equals(EVENT_SET_TIME)) {
                    sendSetTime();
                }
                break;
            case CMD_TAKE:
                // 拍照
                if (bean.getEvent().equals(EVENT_TAKE_PHOTO)) {
                    sendTakePhoto();
                }
                break;
            case CMD_PHOTO_ID:
                // 上传图片
                if (bean.getEvent().equals(EVENT_UPLOAD_PHOTO)) {
                    sendUploadPhoto();
                }
                break;
            case CMD_VIDEO_MODE_RECYCLE:
                // 录像方式（回收）
                if (bean.getEvent().equals(EVENT_RECORD_MODE)) {
                    sendRecordModeRecy();
                }
                break;
            case CMD_VIDEO_MODE_UNIDIRECTIONAL:
                // 录像方式（单向）
                if (bean.getEvent().equals(EVENT_RECORD_MODE)) {
                    sendRecordModeUnid();
                }
                break;
            default:
                break;
        }
    }

    // TODO: 以下 send() 皆是发送数据给服务器相关方法

    private void sendHeartLinkInfo(WebSocket webSocket) {
        // 开机心跳发送设备信息给后台
        SendHeartLinkBean infoBean = new SendHeartLinkBean();
        infoBean.setCmd(VALUE_CONNECT);
        infoBean.setPort(VALUE_DEV);
        infoBean.setEvent(EVENT_DEVICE_REQ_CONNECTION);
        infoBean.setDevice(mDeviceNum);
        infoBean.setAction(VALUE_CONNECT);
        String toServerJson = mGson.toJson(infoBean);
        Log.i(TAG_WEB_SOCKET, "Send HeartLinkInfo JSON--->" + toServerJson);
        webSocket.send(toServerJson);
    }

    private void sendDeviceReqConn() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送设备请求连接状态给后台
                        SendDeviceRequestConnBean infoBean = new SendDeviceRequestConnBean();
                        infoBean.setCmd(VALUE_CONNECT);
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_GET_DEVICE_STATE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setAction(VALUE_CONNECT);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send DeviceRequestConnect JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendDeviceReqDisconn() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送设备请求连接状态给后台
                        SendDeviceRequestConnBean infoBean = new SendDeviceRequestConnBean();
                        infoBean.setCmd(VALUE_DISCONNECT);
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_GET_DEVICE_STATE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setAction(VALUE_DISCONNECT);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send DeviceRequestDisconnect JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendAppBindDevice() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送便携设备和 App 绑定状态给后台
                        SendDeviceBindAppBean infoBean = new SendDeviceBindAppBean();
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_APP_BIND_DEVICE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setMacWifi(mMacWifi);
                        infoBean.setMacPhone(mMacPhone);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send AppBindDevice JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendDeviceInfo() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送设备信息给后台
                        SendGetDeviceInfoBean infoBean = new SendGetDeviceInfoBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_GET_DEVICE_INFO);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setID(mDeviceId);
                        infoBean.setMacWifi(mMacWifi);
                        infoBean.setMacPhone(mMacPhone);
                        infoBean.setModel(mModel);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send GetDeviceInfo JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendDeviceState() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送设备状态给后台
                        SendGetDeviceStatusBean infoBean = new SendGetDeviceStatusBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_GET_DEVICE_STATE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setWarn("LowPower");
                        infoBean.setRecordState("Recording");
                        infoBean.setPushStreamState("Pushing");
                        infoBean.setShareState("Sharing");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send GetDeviceState JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendSensorData() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送传感器信息给后台
                        SendGetSensorInfoBean infoBean = new SendGetSensorInfoBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_GET_SENSOR_DATA);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setGPSLongitude("");
                        infoBean.setGPSLatitude("");
                        infoBean.setGPSAccuracy("");
                        infoBean.setGPSAltitude("");
                        infoBean.setGPSSpeed("");
                        infoBean.setGsensor("");
                        infoBean.setCompass("");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send GetSensorInfo JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendRecordStart() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送开始录像信息给后台
                        SendRecordStartBean infoBean = new SendRecordStartBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_DEVICE_RECORDING);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_RECORD_START);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send RecordStart JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendRecordStop() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送停止录像信息给后台
                        SendRecordStartBean infoBean = new SendRecordStartBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_DEVICE_RECORDING);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_RECORD_STOP);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send RecordStop JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendPushStream() {
//        try {
//            mIPush.start_push("rtmp://live.laiqucc.cn/TestApp/1");
//            if (!mIPush.is_pushing()) {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送设备开始推流信息给后台
                        SendPushStreamBean infoBean = new SendPushStreamBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_PUSH_STREAM);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_PUSH_START);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send PushStream JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

    }

    private void sendStopStream() {
//        try {
//            mIPush.stop_push();
//            mClient.websocket(
//                    Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
//                        @Override
//                        public void onCompleted(Exception ex, WebSocket webSocket) {
//                            if (ex != null) {
//                                ex.printStackTrace();
//                                return;
//                            }
//
//                            // 发送设备停止推流信息给后台
//                            SendPushStreamBean infoBean = new SendPushStreamBean();
//                            infoBean.setStatus(VALUE_SUCCESS);
//                            infoBean.setReason("no reason");
//                            infoBean.setPort(VALUE_DEV);
//                            infoBean.setEvent(EVENT_PUSH_STREAM);
//                            infoBean.setDevice(mDeviceNum);
//                            infoBean.setCmd(CMD_PUSH_STOP);
//                            String toServerJson = mGson.toJson(infoBean);
//                            Log.i(TAG_WEB_SOCKET, "Send StopStream JSON--->" + toServerJson);
//                            webSocket.send(toServerJson);
//                        }
//                    }
//            );
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    private void sendFlashLight() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送灯光闪烁检测信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_DEVICE_TWINKLE_TEST);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_FLASH_LIGHT);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send FlashLight JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendFlashLightOn() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送灯光闪烁检测信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_DEVICE_TWINKLE_TEST);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_FLASH_LIGHT_ON);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send FlashLightOn JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendFlashLightOff() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送灯光闪烁检测信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_DEVICE_TWINKLE_TEST);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_FLASH_LIGHT_OFF);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send FlashLightOff JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendSetLimit() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送限制参数控制信息给后台
                        SendParameterControlBean infoBean = new SendParameterControlBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_SET_LIMIT);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_LIMIT);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send SetLimit JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendSetTime() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送时间校准信息给后台
                        SendTimeCalibrationBean infoBean = new SendTimeCalibrationBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_SET_TIME);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_SYNC_TIME);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send SetTime JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendTakePhoto() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送拍照信息给后台
                        SendPhotographBean infoBean = new SendPhotographBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_TAKE_PHOTO);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_TAKE);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send TakePhoto JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendUploadPhoto() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送上传图片信息给后台
                        SendUploadImgBean infoBean = new SendUploadImgBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_UPLOAD_PHOTO);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_PHOTO_ID);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send UploadPhoto JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendRecordModeRecy() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送录像方式信息给后台
                        SendVideoModeBean infoBean = new SendVideoModeBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_RECORD_MODE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_VIDEO_MODE_RECYCLE);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send RecordModeRecy JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendRecordModeUnid() {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送录像方式信息给后台
                        SendVideoModeBean infoBean = new SendVideoModeBean();
                        infoBean.setStatus(VALUE_SUCCESS);
                        infoBean.setReason("no reason");
                        infoBean.setPort(VALUE_DEV);
                        infoBean.setEvent(EVENT_RECORD_MODE);
                        infoBean.setDevice(mDeviceNum);
                        infoBean.setCmd(CMD_VIDEO_MODE_UNIDIRECTIONAL);
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send RecordModeUnid JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    // TODO: 以下有关串口功能，暂时弃用

    /**
     * 点击录像
     */
    public void onClickRecord(View view) {
        if (mSerialPort != null) {
            OutputStream os = mSerialPort.getOutputStream();
            if (os != null) {
                try {
                    os.write("ks2\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(mSerialPort.getInputStream()));
            while (!isInterrupted()) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        onDataReceived(line);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void onDataReceived(String line) {
        Log.d(TAG_SERIAL_PORT, line);
    }

    /**
     * 打开串口
     */
    private void openSerialPort() {
        try {
            mSerialPort = new SerialPort("/dev/ttyMT3", 115200, 0);
            mReadThread = new ReadThread();
            mReadThread.start();
            Log.i(TAG_SERIAL_PORT, "打开串口成功！");
        } catch (IOException e) {
            e.printStackTrace();
            findViewById(R.id.bt_record).setEnabled(false);
            Toast.makeText(this, "打开串口失败！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERIAL_PORT, "打开串口失败！");
        }
    }

    @Override
    protected void onDestroy() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
        super.onDestroy();

        // 取消绑定视频推流服务
        unbindService(connection);
    }

}
