package cn.shicancan.camserial.model;

public class MacBean {
    private String MacWifi;
    private String MacPhone;

    public MacBean(String macWifi, String macPhone) {
        this.MacWifi = macWifi;
        this.MacPhone = macPhone;
    }

    public String getMacWifi() {
        return MacWifi;
    }

    public void setMacWifi(String macWifi) {
        this.MacWifi = macWifi;
    }

    public String getMacPhone() {
        return MacPhone;
    }

    public void setMacPhone(String macPhone) {
        this.MacPhone = macPhone;
    }

}
