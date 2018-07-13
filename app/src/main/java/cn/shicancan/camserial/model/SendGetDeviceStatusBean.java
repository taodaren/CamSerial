package cn.shicancan.camserial.model;

/**
 * 获取设备状态（设备 → 服务器）
 */

public class SendGetDeviceStatusBean {
    private String Status;                 // 状态
    private String Reason;                 // 原因
    private String Port;                   // 标识来源端
    private String Event;                  // 事件
    private String Device;                 // 设备号
    private String Warn;                   // 警告
    private String RecordState;            // 记录状态
    private String PushStreamState;        // 推流状态
    private String ShareState;             // 分享状态

    public SendGetDeviceStatusBean() {
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

    public String getWarn() {
        return Warn;
    }

    public void setWarn(String warn) {
        Warn = warn;
    }

    public String getRecordState() {
        return RecordState;
    }

    public void setRecordState(String recordState) {
        RecordState = recordState;
    }

    public String getPushStreamState() {
        return PushStreamState;
    }

    public void setPushStreamState(String pushStreamState) {
        PushStreamState = pushStreamState;
    }

    public String getShareState() {
        return ShareState;
    }

    public void setShareState(String shareState) {
        ShareState = shareState;
    }
}
