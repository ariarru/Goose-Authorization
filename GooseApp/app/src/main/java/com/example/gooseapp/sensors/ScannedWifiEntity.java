package com.example.gooseapp.sensors;

public class ScannedWifiEntity {

    private String SSIDValue;
    private String MACValue;
    private String RSSIValue;

    public ScannedWifiEntity(String ssid, String mac, String rssi){
        this.SSIDValue = ssid;
        this.MACValue = mac;
        this.RSSIValue = rssi;
    }

    @Override
    public String toString() {
        return "{\n" +
                "SSID:" + SSIDValue + ",\n" +
                "MAC:'" + MACValue + "',\n" +
                "RSSI:'" + RSSIValue + "'\n" +
                '}';
    }

    public String getSSIDValue() {
        return SSIDValue;
    }

    public void setSSIDValue(String SSIDValue) {
        this.SSIDValue = SSIDValue;
    }

    public String getMACValue() {
        return MACValue;
    }

    public void setMACValue(String MACValue) {
        this.MACValue = MACValue;
    }

    public String getRSSIValue() {
        return RSSIValue;
    }

    public void setRSSIValue(String RSSIValue) {
        this.RSSIValue = RSSIValue;
    }




}
