package com.example.gooseapp.service;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GooseRequest {

    private static final String url= "https://172.20.10.3:5001"; //10.201.63.59:5001"; // indirizzo almawifi
            //"https://192.168.1.225:5001"; //casa
    private static final String fingerprintUrl = "/api/fingerprint";
    private static final String bluetoothUrl = "/api/controlloBle";
    private static RequestQueue queue;
    private static BackgroundService backgroundService;
    
    // Add timeout constants
    private static final int SOCKET_TIMEOUT_MS = 30000; // 30 seconds timeout
    private static final int MAX_RETRIES = 2;

    public GooseRequest(Context context, BackgroundService service){
        this.queue = Volley.newRequestQueue(context, new CustomSSLSocketFactory());

        this.backgroundService = service;
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
                                int roomId = response.getInt("predicted_room");
                                String roomName = response.getString("room_name");
                                Log.i("GOOSE CHECK", "Ho trovato "+ roomId + "-"+ roomName);
                                //save datas
                                backgroundService.saveData(roomId, roomName);
                                boolean checkBLEconnections = response.getBoolean("contr_ble");
                                if(checkBLEconnections){
                                    backgroundService.neededBLE();
                                }
                            } catch (JSONException e) {
                                if (response.has("result")) {
                                    int errore = response.getInt("result");
                                    if (errore == 40) {
                                        Log.e("GOOSE REQUEST", "Cannot find predicted room value");
                                    } else {
                                        Log.e("GOOSE REQUEST", "The user is not authorized to access this room");
                                        backgroundService.sendBasicNotification("Unauthorized user", "You are not authorized to access this room");
                                    }
                                } else {
                                    Log.e("GOOSE REQUEST", "Unexpected JSON structure", e);
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage;
                            if (error instanceof TimeoutError) {
                                errorMessage = "Request timed out. Please check your connection.";
                            } else if (error instanceof com.android.volley.NoConnectionError) {
                                errorMessage = "No network connection available.";
                            } else if (error instanceof com.android.volley.AuthFailureError) {
                                errorMessage = "Authentication failure.";
                            } else if (error instanceof com.android.volley.ServerError) {
                                errorMessage = "Server error occurred.";
                            } else if (error instanceof com.android.volley.NetworkError) {
                                errorMessage = "Network error occurred.";
                            } else {
                                errorMessage = "An unknown error occurred.";
                            }
                            Log.e("GOOSE REQUEST", errorMessage, error);
                        }
                    });
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    SOCKET_TIMEOUT_MS,
                    MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                jsonBody.put("lista_disp", bleData);

            } else {
                jsonBody.put("lista_disp", 101);
            }
            jsonBody.put("user_id", userId);
            jsonBody.put("room_id", roomId);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url + bluetoothUrl, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                            Log.i("GOOSE RESPONSE", response.toString());
                            try {
                                int responseValue = response.getInt("response");
                                Log.i("GOOSE RESPONSE", "Response value: " + responseValue);
                                /*
                                *    AREA_NOT_RESTRICTED = 21 // no notifica
                                    MISSING_DEVICE = 30
                                    EXIT_MISSING_DEVICE = 31
                                    WRONG_DEVICE = 32
                                    HAS_RIGHT_DEVICES = 33 //no notifica
                                    EXTRA_DEVICES= 35 //no notifica
                                    * */

                                switch(responseValue){
                                    case 30 : backgroundService.sendBasicNotification("Missing Device", "You are in a restricted area and you do not have the necessary devices");
                                        break;
                                    case 32 : backgroundService.sendBasicNotification("Wrong Device", "You are in a restricted area and have the wrong devices");
                                        break;

                                }

                            } catch (JSONException e) {
                                Log.e("GOOSE REQUEST", "Error parsing response", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage;
                            if (error instanceof TimeoutError) {
                                errorMessage = "Request timed out. Please check your connection.";
                            } else if (error instanceof com.android.volley.NoConnectionError) {
                                errorMessage = "No network connection available.";
                            } else if (error instanceof com.android.volley.AuthFailureError) {
                                errorMessage = "Authentication failure.";
                            } else if (error instanceof com.android.volley.ServerError) {
                                errorMessage = "Server error occurred.";
                            } else if (error instanceof com.android.volley.NetworkError) {
                                errorMessage = "Network error occurred.";
                            } else {
                                errorMessage = "An unknown error occurred.";
                            }
                            Log.e("GOOSE REQUEST", errorMessage, error);
                        }
                    });

            queue.add(jsonRequest);
        } catch (JSONException e) {
            Log.e("GOOSE REQUEST", "Error creating JSON", e);
        }
    }
}