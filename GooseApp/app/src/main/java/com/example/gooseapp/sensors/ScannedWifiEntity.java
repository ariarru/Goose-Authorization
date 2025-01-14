package com.example.gooseapp.sensors;

import android.net.MacAddress;
import android.net.wifi.WifiSsid;

import java.util.HashMap;
import java.util.Map;

public class ScannedWifiEntity {

    private String SSIDValue;
    private String MACValue;
    private String RSSIValue;

    public ScannedWifiEntity(WifiSsid ssid, MacAddress mac, int rssi){

        this.SSIDValue = ssid == null ? "Not Present" : ssid.toString();
        this.MACValue = mac == null ? "Not Present" : mac.toString();
        this.RSSIValue = String.valueOf(rssi);
    }

    @Override
    public String toString() {
        return "{\n" +
                "SSID:" + SSIDValue + ",\n" +
                "MAC:'" + MACValue + "',\n" +
                "RSSI:'" + RSSIValue + "'\n" +
                '}';
    }

    public Map<String, String> toStringMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("SSID", SSIDValue);
        map.put("MAC", MACValue);
        map.put("RSSI", RSSIValue);
        return map;
    }

}
