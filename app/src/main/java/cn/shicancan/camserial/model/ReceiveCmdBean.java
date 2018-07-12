package cn.shicancan.camserial.model;

/**
 * 接收命令（服务器 → 设备）
 */

public class ReceiveCmdBean {
    private String Cmd;
    private String Event;
    private String Resolution;

    public ReceiveCmdBean() {
    }

    public String getCmd() {
        return Cmd;
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getResolution() {
        return Resolution;
    }

    public void setResolution(String resolution) {
        Resolution = resolution;
    }
}
