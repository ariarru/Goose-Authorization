package com.example.gooseapp.sensors;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

public class ScannedBLEEntity {

    private BluetoothDevice bluetoothDevice;
     private String name;

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
}
