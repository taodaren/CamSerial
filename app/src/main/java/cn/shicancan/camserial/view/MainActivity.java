package cn.shicancan.camserial.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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
import cn.shicancan.camserial.model.MacBean;
import cn.shicancan.camserial.presenter.Urls;

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
    private String mMacWifi, mMacPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtvPhoneInfo = findViewById(R.id.tv_phone_info);
        mMacWifi = getConnectedWifiMacAddress(this);
        mMacPhone = getMacAddress(this);

        Log.i(TAG_TYC, "mMacWifi: " + mMacWifi);
        Log.i(TAG_TYC, "mMacPhone: " + mMacPhone);

        getPhoneInfo();
        setWebSocket();
//        openSerialPort();
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
    }

    /**
     * 获取手机信息
     */
    @SuppressLint("SetTextI18n")
    private void getPhoneInfo() {
        mtvPhoneInfo.setText("品牌：" + getPhoneBrand() + "\n");
        mtvPhoneInfo.append("型号：" + getPhoneModel() + "\n");
        mtvPhoneInfo.append("版本：Android " + android.os.Build.VERSION.RELEASE + "\n\n");
        mtvPhoneInfo.append("手机号：" + getPhoneNum(this) + "\n");
        mtvPhoneInfo.append("运营商：" + getPhoneOperator(this) + "\n\n");
        mtvPhoneInfo.append("IMEI：" + getPhoneIMEI(this) + "\n");
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
     * 通过 WebSocket 实现长连接通讯消息
     */
    private void setWebSocket() {
        AsyncHttpClient.getDefaultInstance().websocket(
                Urls.WEB_SOCKET, Urls.PORT,
                new AsyncHttpClient.WebSocketConnectCallback() {
                    @Override
                    public void onCompleted(Exception ex, WebSocket webSocket) {
                        if (ex != null) {
                            ex.printStackTrace();
                            return;
                        }
                        // 发送 json 字符串给后台
                        webSocket.send(new Gson().toJson(new MacBean(mMacWifi, mMacPhone)));
                        // 接收后台数据后的操作，该方法是个线程，如果要修改页面，记得放在主线程里
                        webSocket.setStringCallback(new WebSocket.StringCallback() {
                            @Override
                            public void onStringAvailable(String s) {
                                Log.i(TAG_WEB_SOCKET, "来自于服务器的信息--->" + s);
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

}
