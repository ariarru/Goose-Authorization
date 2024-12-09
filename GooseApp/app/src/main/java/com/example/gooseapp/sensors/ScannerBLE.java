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

import com.example.gooseapp.service.BackgroundService;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ScannerBLE {

    private Context bleContext;
    private BackgroundService service;
    //BLUETOOTH
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private static final long STOP_SCAN_PERIOD = 2 * 1000; //3 SECONDI
    private boolean scanFoundResults = false;
    private boolean isBLEScanning;
    private Handler bluetoothHandler = new Handler();

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            // Non facciamo nulla qui, gestiamo tutto in onBatchScanResults
            // Qui mi servirebbe solo per gestirne uno
            ScannedBLEEntity scannedBLE = new ScannedBLEEntity(result.getDevice());
            Log.i("GOOSE SIGNAL BLE", scannedBLE.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i("BLE GOOSE", "sono dentro il batch con questi valori:");
            Log.i("BLE GOOSE", results.toString());
            scanFoundResults = true;

            List<ScannedBLEEntity> scannedDevices = new ArrayList<>();
            for (ScanResult result : results) {
                ScannedBLEEntity scannedBLE = new ScannedBLEEntity(result.getDevice());
                scannedDevices.add(scannedBLE);
                Log.i("GOOSE SIGNAL BLE", "Dispositivo trovato: " + scannedBLE.toString());

                //DISTANZA:  RECUPERA RSSI E METTILO NEL FILE
                JsonObject rssiObject = new JsonObject();
                rssiObject.addProperty("rssi", result.getRssi()); // Aggiungi RSSI al JSON
                rssiArray.add(rssiObject);

            }
            // DISTANZA: Scriviamo i dati RSSI nel file JSON
            saveRssiDataToFile(rssiArray);

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

    public ScannerBLE(Context context, BackgroundService service) {
        this.bleContext = context;
        this.service = service;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
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
            if (ActivityCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(bleContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                Log.e("BLE SIGNAL", "Non ho i permessi bluetooth");
                service.sendBasicNotification("ERROR in BLE measuring", "I need bluetooth permissions");// Notifica il service del fallimento
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
                            System.out.println("ora dovrei inviare i dati al backend e gestire la risposta");
                            service.manageBLEScans(null);
                        } else {
                            Log.i("BLE GOOSE", "Dispositivi trovati durante la scansione");
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

    //DISTANZA: Metodo per salvare i dati RSSI nel file JSON
    private void saveRssiDataToFile(JsonArray rssiData) {
        FileOutputStream fos = null;
        try {
            // Crea o apri il file val_rssi.json
            File file = new File(bleContext.getFilesDir(), "val_rssi.json");

            // Scrivi i dati RSSI nel file
            fos = new FileOutputStream(file, true); // Imposta 'true' per appendere i dati
            Gson gson = new Gson();
            String json = gson.toJson(rssiData); // Converte l'array JSON in stringa

            fos.write(json.getBytes()); // Scrive nel file
            fos.flush();
            Log.i("GOOSE SIGNAL BLE", "RSSI salvato nel file: " + json);

        } catch (IOException e) {
            Log.e("GOOSE SIGNAL BLE", "Errore durante la scrittura del file RSSI", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close(); // Chiudi il flusso
                } catch (IOException e) {
                    Log.e("GOOSE SIGNAL BLE", "Errore durante la chiusura del file", e);
                }
            }
        }
    }


}


