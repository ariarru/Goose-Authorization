package com.example.gooseapp.service;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gooseapp.R;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooseRequest {

    private static final String url= "https://192.168.1.225:5001";
    private static final String fingerprintUrl = "/api/fingerprint";
    private static final String bluetoothUrl = "/api/controlloBle";
    private static RequestQueue queue;
    private static BackgroundService backgroundService;
    private static SharedPreferences sharedPreferences;


    public GooseRequest(Context context, BackgroundService service){
        this.queue = Volley.newRequestQueue(context, new CustomSSLSocketFactory());
        this.backgroundService = service;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void sendWifiScan(List<ScannedWifiEntity> swes, int userId){
        try {
            // Creare l'oggetto JSON da inviare
            JSONObject jsonBody = new JSONObject();
            JSONObject wifiData = new JSONObject();
            
            // Convertire la lista di ScannedWifiEntity nel formato atteso dal backend
            for(int i = 0; i < swes.size(); i++) {
                JSONObject wifiAP = new JSONObject();
                Map<String, String> data = swes.get(i).toStringMap();
                for(Map.Entry<String, String> entry : data.entrySet()) {
                    wifiAP.put(entry.getKey(), entry.getValue());
                }
                wifiData.put("ap" + i, wifiAP);
            }
            
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("wifi_data", wifiData);
            
            jsonBody.put("user_id", userId);
            jsonBody.put("json_input", jsonInput);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url + fingerprintUrl, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                            Log.i("GOOSE RESPONSE", response.toString());
                            try {
                                int room = response.getInt("predicted_room");
                                Log.i("GOOSE CHECK", "Ho trovato "+ room);

                                sharedPreferences.edit().putInt("room_in", room).apply();
                                Log.i("GOOSE CHECK", "Mi sono salvato "+sharedPreferences.getInt("room_in", -3));
                                boolean checkBLEconnections = response.getBoolean("contr_ble");
                                if(checkBLEconnections){
                                    backgroundService.neededBLE();
                                }
                            } catch (JSONException e) {
                                Log.e("GOOSE REQUEST", "cannot find predicted room value");
                                throw new RuntimeException(e);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("GOOSE REQUEST", String.valueOf(error));
                        }
                    });

            queue.add(jsonRequest);
        } catch (JSONException e) {
            Log.e("GOOSE REQUEST", "Error creating JSON", e);
        }
    }

    public static void sendBLEScan(List<ScannedBLEEntity> sbes, int userId, int roomId){
        try {
            JSONObject jsonBody = new JSONObject();
            JSONObject bleData = new JSONObject();
            
            // Convertire la lista di ScannedBLEEntity nel formato atteso dal backend
            if (sbes != null) {
                for(int i = 0; i < sbes.size(); i++) {
                    JSONObject bleDevice = new JSONObject();
                    Map<String, String> data = sbes.get(i).toStringMap();
                    for(Map.Entry<String, String> entry : data.entrySet()) {
                        bleDevice.put(entry.getKey(), entry.getValue());
                    }
                    bleData.put("device" + i, bleDevice);
                }
            }
            
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("ble_data", bleData);
            
            jsonBody.put("room_id", roomId);
            jsonBody.put("json_input", jsonInput);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url + bluetoothUrl, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                            Log.i("GOOSE RESPONSE", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("GOOSE REQUEST", String.valueOf(error));
                        }
                    });

            queue.add(jsonRequest);
        } catch (JSONException e) {
            Log.e("GOOSE REQUEST", "Error creating JSON", e);
        }
    }
}
