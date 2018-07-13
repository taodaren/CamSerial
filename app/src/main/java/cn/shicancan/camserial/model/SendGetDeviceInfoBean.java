package cn.shicancan.camserial.model;

/**
 * 获取设备信息（设备 → 服务器）
 */

public class SendGetDeviceInfoBean {
    private String Status;          // 状态
    private String Reason;          // 原因
    private String Port;            // 标识来源端
    private String Event;           // 事件
    private String Device;          // 设备号
    private String ID;              // 设备 ID
    private String MacWifi;         // MAC地址（连接路由器）
    private String MacPhone;        // MAC地址（设备）
    private String Model;           // 型号

    public SendGetDeviceInfoBean() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }
}
