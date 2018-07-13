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

    public void setStatus(String status) {
        Status = status;
    }

    public void setReason(String reason) {
        Reason = reason;
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

    public void setCmd(String cmd) {
        Cmd = cmd;
    }
}
