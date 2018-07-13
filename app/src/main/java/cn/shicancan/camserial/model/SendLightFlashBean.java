package cn.shicancan.camserial.model;

/**
 * 灯光闪烁检测（设备 → 服务器）
 */

public class SendLightFlashBean {
    private String Status;          // 状态
    private String Reason;          // 原因
    private String Port;            // 标识来源端
    private String Event;           // 事件
    private String Device;          // 设备号
    private String Cmd;             // 命令

    public SendLightFlashBean() {
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

    public String getCmd() {
        return Cmd;
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
    }
}
