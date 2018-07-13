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

    public void setPort(String port) {
        Port = port;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public void setMacWifi(String macWifi) {
        MacWifi = macWifi;
    }

    public void setMacPhone(String macPhone) {
        MacPhone = macPhone;
    }
}
