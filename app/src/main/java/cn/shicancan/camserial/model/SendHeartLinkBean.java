package cn.shicancan.camserial.model;

/**
 * 开机心跳（设备 → 服务器）
 */

public class SendHeartLinkBean {
    private String Cmd;             // 命令
    private String Port;            // 标识来源端
    private String Event;           // 事件
    private String Device;          // 设备号
    private String Action;          // 需要执行的动作

    public SendHeartLinkBean() {
    }

    public void setCmd(String cmd) {
        Cmd = cmd;
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

    public void setAction(String action) {
        Action = action;
    }

}
