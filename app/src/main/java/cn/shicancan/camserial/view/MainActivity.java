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
import cn.shicancan.camserial.model.SendDeviceRequestConnBean;
import cn.shicancan.camserial.model.SendGetDeviceInfoBean;
import cn.shicancan.camserial.model.SendGetDeviceStatusBean;
import cn.shicancan.camserial.model.SendGetSensorInfoBean;
import cn.shicancan.camserial.model.SendHeartLinkBean;
import cn.shicancan.camserial.model.SendLightFlashBean;
import cn.shicancan.camserial.model.SendPushStreamBean;
import cn.shicancan.camserial.model.SendRecordStartBean;
import cn.shicancan.camserial.presenter.IPushVideoAidlInterface;
import cn.shicancan.camserial.presenter.Urls;

import static cn.shicancan.camserial.app.AppConstant.CMD_DEVICE_INFO;
import static cn.shicancan.camserial.app.AppConstant.CMD_DEVICE_STATE;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT_OFF;
import static cn.shicancan.camserial.app.AppConstant.CMD_FLASH_LIGHT_ON;
import static cn.shicancan.camserial.app.AppConstant.CMD_PUSH_START;
import static cn.shicancan.camserial.app.AppConstant.CMD_PUSH_STOP;
import static cn.shicancan.camserial.app.AppConstant.CMD_RECORD_START;
import static cn.shicancan.camserial.app.AppConstant.CMD_RECORD_STOP;
import static cn.shicancan.camserial.app.AppConstant.CMD_SENSOR_DATA;
import static cn.shicancan.camserial.app.AppConstant.STATUS_BINDING_FAILED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_BINDING_SUCCESS;
import static cn.shicancan.camserial.app.AppConstant.STATUS_CONNECTED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_DISCONNECTED;
import static cn.shicancan.camserial.app.AppConstant.STATUS_WAITING;
import static cn.shicancan.camserial.app.AppConstant.TAG_SERIAL_PORT;
import static cn.shicancan.camserial.app.AppConstant.TAG_TYC;
import static cn.shicancan.camserial.app.AppConstant.TAG_WEB_SOCKET;
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
    protected SerialPort mSerialPort;// 串口
    private ReadThread mReadThread;
    private TextView mtvPhoneInfo;
    private String mModel, mDeviceId, mMacWifi, mMacPhone;
    private Gson mGson;
    private AsyncHttpClient mClient;

    private IPushVideoAidlInterface mPushInterface;
    // 定义推流
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPushInterface = IPushVideoAidlInterface.Stub.asInterface(iBinder);
            // IPushVideoAidlInterface 已经得到，想干什么干什么
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGson = new Gson();
        mClient = AsyncHttpClient.getDefaultInstance();

        mtvPhoneInfo = findViewById(R.id.tv_phone_info);
        mModel = getPhoneModel();
        mDeviceId = getPhoneIMEI(this);
        mMacWifi = getConnectedWifiMacAddress(this);
        mMacPhone = getMacAddress(this);

        Log.i(TAG_TYC, "mDeviceId: " + mDeviceId);
        Log.i(TAG_TYC, "mMacWifi: " + mMacWifi);
        Log.i(TAG_TYC, "mMacPhone: " + mMacPhone);

        getPhoneInfo();
        setWebSocket();
//        openSerialPort();

        // 绑定视频推流服务
        Intent intent = new Intent("cn.shicancan.uvcdirectcamera.IPushVideoAidlInterface");
        intent.setPackage("cn.shicancan.uvcdirectcamera");
        bindService(intent, connection, BIND_AUTO_CREATE);
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
                                Log.i(TAG_WEB_SOCKET, "onDataAvailable: I got some bytes!");
                                // 请注意，此数据已被读取
                                bb.recycle();
                            }
                        });
                    }
                }
        );
    }

    private void handleServerStatus(String jsonSocket) {
        ReceiveStatusBean bean = mGson.fromJson(jsonSocket, ReceiveStatusBean.class);
        switch (bean.getStatus()) {
            case STATUS_CONNECTED:
                // 设备请求连接（连接）
                if (bean.getEvent().equals("DeviceReqConnection")) {
                    sendDeviceReqConn();
                }
                break;
            case STATUS_DISCONNECTED:
                // 设备请求连接（断开）
                if (bean.getEvent().equals("DeviceReqConnection")) {
                    sendDeviceReqDisconn();
                }
                break;
            case STATUS_BINDING_SUCCESS:
                break;
            case STATUS_BINDING_FAILED:
                break;
            case STATUS_WAITING:
                break;
            default:
                break;
        }
    }

    /**
     * 处理服务器发来的命令
     */
    private void handleServerCmd(String jsonSocket) {
        ReceiveCmdBean bean = mGson.fromJson(jsonSocket, ReceiveCmdBean.class);
        switch (bean.getCmd()) {
            case CMD_PUSH_START:
                // 开始推流
                if (bean.getEvent().equals("PushStream")) {
                    sendPushStream();
                }
                break;
            case CMD_PUSH_STOP:
                // 停止推流
                if (bean.getEvent().equals("PushStream")) {
                    sendStopStream();
                }
                break;
            case CMD_FLASH_LIGHT:
                // 灯光闪烁检测（闪烁）
                if (bean.getEvent().equals("DeviceTwinkleTest")) {
                    sendFlashLight();
                }
                break;
            case CMD_FLASH_LIGHT_ON:
                // 灯光闪烁检测（开）
                if (bean.getEvent().equals("DeviceTwinkleTest")) {
                    sendFlashLightOn();
                }
                break;
            case CMD_FLASH_LIGHT_OFF:
                // 灯光闪烁检测（关）
                if (bean.getEvent().equals("DeviceTwinkleTest")) {
                    sendFlashLightOff();
                }
                break;
            case CMD_DEVICE_INFO:
                // 获取设备信息
                if (bean.getEvent().equals("GetDeviceInfo")) {
                    sendDeviceInfo();
                }
                break;
            case CMD_DEVICE_STATE:
                // 获取设备状态
                if (bean.getEvent().equals("GetDeviceState")) {
                    sendDeviceState();
                }
                break;
            case CMD_SENSOR_DATA:
                // 获取传感器数据
                if (bean.getEvent().equals("GetSensorData")) {
                    sendSensorData();
                }
                break;
            case CMD_RECORD_START:
                // 开始录像
                if (bean.getEvent().equals("ConvenientDevicePictureRecording")) {
                    sendRecordStart();
                }
                break;
            case CMD_RECORD_STOP:
                // 停止录像
                if (bean.getEvent().equals("ConvenientDevicePictureRecording")) {
                    sendRecordStop();
                }
                break;
            default:
                break;
        }
    }

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

    private void sendHeartLinkInfo(WebSocket webSocket) {
        // 开机心跳发送设备信息给后台
        SendHeartLinkBean infoBean = new SendHeartLinkBean();
        infoBean.setCmd("Connect");
        infoBean.setPort("Dev");
        infoBean.setEvent("DeviceReqConnection");
        infoBean.setDevice("10006");
        infoBean.setAction("Connect");
        String toServerJson = mGson.toJson(infoBean);
        Log.i(TAG_WEB_SOCKET, "Send HeartLinkInfo JSON--->" + toServerJson);
        webSocket.send(toServerJson);
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
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("DeviceInfo");
                        infoBean.setDevice("10006");
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
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("GetDeviceState");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setWarn("LowPower");
                        infoBean.setRecordState("Recording");
                        infoBean.setPushStreamState("Pushing");
                        infoBean.setShareState("Sharing");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send GetDeviceInfo JSON--->" + toServerJson);
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

                        // 发送设备状态给后台
                        SendGetSensorInfoBean infoBean = new SendGetSensorInfoBean();
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("GetSensorData");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
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
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("ConvenientDevicePictureRecording");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setCmd("RecordStart");
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
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("ConvenientDevicePictureRecording");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setCmd("RecordStop");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send RecordStop JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    private void sendPushStream() {
//        try {
//            mPushInterface.start_push("rtmp://live.laiqucc.cn/TestApp/1");
//            if (!mPushInterface.is_pushing()) {
        mClient.websocket(
                Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }

                        // 发送开始录像信息给后台
                        SendPushStreamBean infoBean = new SendPushStreamBean();
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("PushStream");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setCmd("PushStart");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send PushStart JSON--->" + toServerJson);
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
        try {
            mPushInterface.stop_push();
            mClient.websocket(
                    Urls.WEB_SOCKET, Urls.PORT, new AsyncHttpClient.WebSocketConnectCallback() {
                        @Override
                        public void onCompleted(Exception ex, WebSocket webSocket) {
                            if (ex != null) {
                                ex.printStackTrace();
                                return;
                            }

                            // 发送开始录像信息给后台
                            SendPushStreamBean infoBean = new SendPushStreamBean();
                            infoBean.setStatus("Success");
                            infoBean.setReason("no reason");
                            infoBean.setPort("Dev");
                            infoBean.setEvent("StopStream");
                            // TODO: 2018/7/12 暂时写死
                            infoBean.setDevice("10006");
                            infoBean.setCmd("PushStop");
                            String toServerJson = mGson.toJson(infoBean);
                            Log.i(TAG_WEB_SOCKET, "Send StopStream JSON--->" + toServerJson);
                            webSocket.send(toServerJson);
                        }
                    }
            );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

                        // 发送设备状态给后台
                        SendDeviceRequestConnBean infoBean = new SendDeviceRequestConnBean();
                        infoBean.setCmd("Connect");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("GetDeviceState");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setAction("Connect");
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

                        // 发送设备状态给后台
                        SendDeviceRequestConnBean infoBean = new SendDeviceRequestConnBean();
                        infoBean.setCmd("Disconnect");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("GetDeviceState");
                        // TODO: 2018/7/12 暂时写死
                        infoBean.setDevice("10006");
                        infoBean.setAction("Disconnect");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send DeviceRequestDisconnect JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
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

                        // 发送设备信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("DeviceTwinkleTest");
                        infoBean.setDevice("10006");
                        infoBean.setCmd("FlashLight");
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

                        // 发送设备信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("DeviceTwinkleTest");
                        infoBean.setDevice("10006");
                        infoBean.setCmd("ON");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send FlashLight JSON--->" + toServerJson);
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

                        // 发送设备信息给后台
                        SendLightFlashBean infoBean = new SendLightFlashBean();
                        infoBean.setStatus("Success");
                        infoBean.setReason("no reason");
                        infoBean.setPort("Dev");
                        infoBean.setEvent("DeviceTwinkleTest");
                        infoBean.setDevice("10006");
                        infoBean.setCmd("OFF");
                        String toServerJson = mGson.toJson(infoBean);
                        Log.i(TAG_WEB_SOCKET, "Send FlashLight JSON--->" + toServerJson);
                        webSocket.send(toServerJson);
                    }
                }
        );
    }

    /**
     * 获取手机信息
     */
    @SuppressLint("SetTextI18n")
    private void getPhoneInfo() {
        mtvPhoneInfo.setText("品牌：" + getPhoneBrand() + "\n");
        mtvPhoneInfo.append("型号：" + mModel + "\n");
        mtvPhoneInfo.append("版本：Android " + android.os.Build.VERSION.RELEASE + "\n\n");
        mtvPhoneInfo.append("手机号：" + getPhoneNum(this) + "\n");
        mtvPhoneInfo.append("运营商：" + getPhoneOperator(this) + "\n\n");
        mtvPhoneInfo.append("IMEI：" + mDeviceId + "\n");
//        mtvPhoneInfo.append("IMSI：" + getPhoneIMSI(this) + "\n");
        mtvPhoneInfo.append("MAC_WIFI：" + mMacWifi + "\n");
        mtvPhoneInfo.append("MAC_PHONE：" + mMacPhone + "\n\n");
        mtvPhoneInfo.append("总内存：" + getTotalMemory(this) + "\n");
        mtvPhoneInfo.append("当前可用内存：" + getAvailMemory(this) + "\n\n");
        mtvPhoneInfo.append("CPU 名字：" + getCpuName() + "\n");
        mtvPhoneInfo.append("CPU 最大频率：" + getMaxCpuFreq() + "\n");
        mtvPhoneInfo.append("CPU 最小频率：" + getMinCpuFreq() + "\n");
        mtvPhoneInfo.append("CPU 当前频率：" + getCurCpuFreq() + "\n");
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

}
