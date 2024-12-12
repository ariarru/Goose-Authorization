package com.example.gooseapp.sensors;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

public class ScannedBLEEntity {

    private BluetoothDevice bluetoothDevice;
    private String address;
    private int rssi; // DISTANZA

    @SuppressLint("MissingPermission")
    public ScannedBLEEntity(BluetoothDevice blD, int rssi){
        this.bluetoothDevice = blD;
        this.address = blD.getAddress();
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "{\n" +
                "ADDRESS:" + address + ",\n" +
                "RSSI:" + rssi + ",\n" +
                '}';
    }

    public String getStringRSSI(){
        return this.rssi +"";
    }
    public Map<String, String> toStringMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("ADDRESS", address);
        map.put("RSSI", String.valueOf(rssi)); //DISTANZA
        return map;
    }
}
