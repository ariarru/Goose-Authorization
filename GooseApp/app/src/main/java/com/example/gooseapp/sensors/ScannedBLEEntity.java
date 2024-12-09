package com.example.gooseapp.sensors;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

public class ScannedBLEEntity {

    private BluetoothDevice bluetoothDevice;
    private String name;
    //private int rssi; // DISTANZA

    @SuppressLint("MissingPermission")
    public ScannedBLEEntity(BluetoothDevice blD){
        this.bluetoothDevice = blD;
        //this.address = blD.getAddress();
        this.name = blD.getName();
        //this.rssi = blD.EXTRA_RSSI;
    }

    @Override
    public String toString() {
        return "{\n" +
                "NAME:" + name + ",\n" +
                '}';
    }
    public Map<String, String> toStringMap(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("NAME", name);
        return map;
    }

}







