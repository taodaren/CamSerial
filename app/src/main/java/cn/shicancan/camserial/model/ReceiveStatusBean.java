package cn.shicancan.camserial.model;

/**
 * 接收状态（服务器 → 设备）
 */

public class ReceiveStatusBean {
    private String Status;
    private String Event;

    public ReceiveStatusBean() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }
}
