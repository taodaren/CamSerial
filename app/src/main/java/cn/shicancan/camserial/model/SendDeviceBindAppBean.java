package cn.shicancan.camserial.model;

/**
 * 便携设备和 App 绑定（设备 → 服务器）
 */

public class SendDeviceBindAppBean {
    private String Port;                   // 标识来源端
    private String Event;                  // 事件
    private String Device;                 // 设备号
    private String MacWifi;                // MAC地址（连接路由器）
    private String MacPhone;               // MAC地址（设备）

    public SendDeviceBindAppBean() {
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getMacWifi() {
        return MacWifi;
    }

    public void setMacWifi(String macWifi) {
        MacWifi = macWifi;
    }

    public String getMacPhone() {
        return MacPhone;
    }

    public void setMacPhone(String macPhone) {
        MacPhone = macPhone;
    }
}
