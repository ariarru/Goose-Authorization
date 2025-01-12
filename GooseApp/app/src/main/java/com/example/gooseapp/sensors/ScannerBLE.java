package com.example.gooseapp.sensors;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;

import com.example.gooseapp.service.BackgroundService;

import java.util.ArrayList;
import java.util.List;



public class ScannerBLE {

    private Context bleContext;
    private BackgroundService service;
    //BLUETOOTH
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private static final long STOP_SCAN_PERIOD = 500; //mezzo secondo (0.5 * 1000)
    private boolean scanFoundResults = false;
    private boolean isBLEScanning;
    private Handler bluetoothHandler = new Handler();
    private List<ScannedBLEEntity> scannedDevices = new ArrayList<>();

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            // Non facciamo nulla qui, gestiamo tutto in onBatchScanResults
            // Qui mi servirebbe solo per gestirne uno
            if(result != null) {
                scanFoundResults = true;
            }
            ScannedBLEEntity scannedBLE = new ScannedBLEEntity(result.getDevice(), result.getRssi());
            scannedDevices.add(scannedBLE);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i("BLE GOOSE", "sono dentro il batch con questi valori:");
            scanFoundResults = true;

            for (ScanResult result : results) {
                ScannedBLEEntity scannedBLE = new ScannedBLEEntity(result.getDevice(), result.getRssi());
                scannedDevices.add(scannedBLE);
            }

            // Invia tutti i dispositivi trovati al service
            service.manageBLEScans(scannedDevices);

            isBLEScanning = false;
            scanFoundResults = false;
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("GOOSE SIGNAL BLE RESULT", "Scan endend in error with code:" + errorCode);
            service.sendBasicNotification("ERROR in BLE measuring", "Scan endend in error with code:" + errorCode);// Notifica il service del fallimento
            isBLEScanning = false;
        }
    };

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1001;

    public ScannerBLE(Context context, BackgroundService service) {
        this.bleContext = context;
        this.service = service;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private boolean checkBluetoothPermissions() {
        return ContextCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        if (bleContext instanceof android.app.Activity) {
            ActivityCompat.requestPermissions(
                (android.app.Activity) bleContext, 
                new String[]{
                    Manifest.permission.BLUETOOTH_SCAN, 
                    Manifest.permission.BLUETOOTH_CONNECT
                }, 
                REQUEST_BLUETOOTH_PERMISSIONS
            );
        } else {
            Log.e("BLE SIGNAL", "Cannot request Bluetooth permissions: context is not an Activity");
        }
    }

    @SuppressWarnings("MissingPermission")
    public void backgroundMeasureBLE() {
        if (!isBLEScanning) {
            // Verifica se il Bluetooth è abilitato
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.e("GOOSE SIGNAL BLE", "Bluetooth non abilitato");
                service.manageBLEScans(null);
                return;
            }

            // Verifica i permessi
            if (!checkBluetoothPermissions()) {
                Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                requestBluetoothPermissions();
                service.sendBasicNotification("ERROR in BLE measuring", "I need bluetooth permissions");
                return;
            }

            scanFoundResults = false;
            isBLEScanning = true;

            // Avvia la scansione
            Log.i("BACKGROUND GOOSE SIGNAL", "start measuring BLE");
            bluetoothLeScanner.startScan(leScanCallback);
            Log.i("GOOSE SIGNAL BLE", "Avviata scansione BLE");

            // Handler per fermare la scansione dopo STOP_SCAN_PERIOD
            bluetoothHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isBLEScanning) {
                        isBLEScanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);
                        Log.i("GOOSE SIGNAL BLE", "Scansione BLE terminata per timeout");

                        if (!scanFoundResults) {
                            Log.i("BLE GOOSE", "non ho trovato nulla");
                            service.manageBLEScans(null);
                        } else {
                            Log.i("BLE GOOSE", "Dispositivi trovati durante la scansione");
                            // Invia tutti i dispositivi trovati al service
                            service.manageBLEScans(scannedDevices);

                            isBLEScanning = false;
                            scanFoundResults = false;
                        }
                    }
                }
            }, STOP_SCAN_PERIOD);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopBLE() {
        if (isBLEScanning) {
            isBLEScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.i("GOOSE SIGNAL BLE", "Scansione BLE interrotta manualmente");
        }
    }
}
