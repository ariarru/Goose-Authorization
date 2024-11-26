package com.example.gooseapp.sensors;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.gooseapp.service.BackgroundService;

import java.util.List;

public class ScannerBLE {

    private Context bleContext;
    private BackgroundService service;
    //BLUETOOTH
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private static final long STOP_SCAN_PERIOD = 3 * 1000; //3 SECONDI
    private boolean scanFoundResults = false;
    private boolean isBLEScanning;
    private Handler bluetoothHandler = new Handler();
    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            scanFoundResults= true;
            ScannedBLEEntity scannedBLE = new ScannedBLEEntity(result.getDevice());
            Log.i("BACKGROUND GOOSE SIGNAL", scannedBLE.toString());
            isBLEScanning = false;
            //definisci cosa fare con i ble results
            //...
            scanFoundResults= false;
        }

        @Override
        public void onScanFailed(int errorCode){
            super.onScanFailed(errorCode);
            Log.e("GOOSE SIGNAL BLE RESULT", "Scan endend in error with code:"+errorCode);
        }

        @Override
        public void onBatchScanResults (List<ScanResult> results){
            super.onBatchScanResults(results);
            Log.i("BLE GOOSE", "sono dentro il batch con questi valori:");
            Log.i("BLE GOOSE", results.toString());

        }
    };

    public ScannerBLE(Context context, BackgroundService service){
        this.bleContext = context;
        this.service = service;
        bluetoothAdapter = ((BluetoothManager) bleContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    //misura ble
    public void backgroundMeasureBLE(){
        if (!isBLEScanning) {
            if (ActivityCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                return;
            }
            // Stops scanning after a predefined scan period.
            bluetoothHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isBLEScanning = false;
                    if (ActivityCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                    //controlla se ha trovato qualcosa
                    if (!scanFoundResults) {
                        Log.i("BLE GOOSE", "non ho trovato nulla");
                        System.out.println("ora dovrei inviare i dati al backend e gestire la risposta");
                    } else {
                        Log.i("BLE GOOSE", "Dispositivi trovati durante la scansione");
                    }
                }
            }, STOP_SCAN_PERIOD);
            isBLEScanning = true;
            Log.i("BACKGROUND GOOSE SIGNAL", "start measuring BLE");
            bluetoothLeScanner.startScan(leScanCallback);
        }
    }

}
