package cn.shicancan.camserial.model;

/**
 * 获取传感器信息（设备 → 服务器）
 */

public class SendGetSensorInfoBean {
    private String Status;              // 状态
    private String Reason;              // 原因
    private String Port;                // 标识来源端
    private String Event;               // 事件
    private String Device;              // 设备号
    private String GPSLongitude;        // GPS 经度
    private String GPSLatitude;         // GPS 纬度
    private String GPSAccuracy;         // GPS 准确度
    private String GPSAltitude;         // GPS 海拔高度
    private String GPSSpeed;            // GPS 速度
    private String Gsensor;             // 传感器
    private String Compass;             // 罗盘

    public SendGetSensorInfoBean() {
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

    public String getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(String GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public String getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(String GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public String getGPSAccuracy() {
        return GPSAccuracy;
    }

    public void setGPSAccuracy(String GPSAccuracy) {
        this.GPSAccuracy = GPSAccuracy;
    }

    public String getGPSAltitude() {
        return GPSAltitude;
    }

    public void setGPSAltitude(String GPSAltitude) {
        this.GPSAltitude = GPSAltitude;
    }

    public String getGPSSpeed() {
        return GPSSpeed;
    }

    public void setGPSSpeed(String GPSSpeed) {
        this.GPSSpeed = GPSSpeed;
    }

    public String getGsensor() {
        return Gsensor;
    }

    public void setGsensor(String gsensor) {
        Gsensor = gsensor;
    }

    public String getCompass() {
        return Compass;
    }

    public void setCompass(String compass) {
        Compass = compass;
    }
}
