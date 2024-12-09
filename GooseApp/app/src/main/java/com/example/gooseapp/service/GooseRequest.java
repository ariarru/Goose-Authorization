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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;



public class GooseRequest {

    private static final String url= "192.168.1.19:5001"; //10.201.63.59:5001"; // indirizzo almawifi
            //"https://192.168.1.225:5001"; //casa "https://172.20.10.3:5001"
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

        //LATENZA: Memorizziamo il tempo di inizio prima di inviare la richiesta
        long startTime = System.currentTimeMillis();


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
                                Log.e("GOOSE REQUEST", "cannot find predicted room value");
                                throw new RuntimeException(e);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage;
                            
                            // Check if the error has a network response with data
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject errorObj = new JSONObject(jsonError);
                                    if (errorObj.has("code")) {
                                        //LATENZA: Memorizziamo il tempo di fine prima di inviare la notifica accesso non autorizzato
                                        long endTime = System.currentTimeMillis();
                                        //LATENZA: Calcola la latenza e Crea il dato di latenza come oggetto JSON
                                        long latency = endTime - startTime;
                                        JSONObject latencyData = new JSONObject();
                                        latencyData.put("latency", latency);

                                        //LATENZA: memorizza risultati
                                        // Aggiungi il nuovo dato alla fine del file
                                        File file = new File(context.getFilesDir(), "val_latency.json"); 
                                        try {
                                            FileWriter fileWriter = new FileWriter(file, true); 
                                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                                            // Scrivi il nuovo dato JSON nel file, seguito da una nuova riga
                                            bufferedWriter.write(latencyData.toString());
                                            bufferedWriter.newLine();  // Vai a capo

                                            // Chiudi il file dopo la scrittura
                                            bufferedWriter.close();
                                            System.out.println("Latenza aggiunta con successo nel file.");
                                        } catch (IOException e) {
                                            Log.e("GOOSE REQUEST", "Errore nella scrittura della latenza nel file", e);
                                        }

                                        backgroundService.sendBasicNotification("NOT AUTHORIZED", "User not authorized to enter the room");
                                        Log.e("GOOSE REQUEST", "User not authorized");
                                        return;
                                    }
                                } catch (JSONException e) {
                                    Log.e("GOOSE REQUEST", "Error parsing error response", e);
                                }
                            }

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
                                    case 31 : backgroundService.sendBasicNotification("Missing Device", "you are leaving a restricted area without the security devices");
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
                            
                            // Check if the error has a network response with data
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject errorObj = new JSONObject(jsonError);
                                    if (errorObj.has("code")) {
                                        Log.e("GOOSE REQUEST", "quack");
                                        return;
                                    }
                                } catch (JSONException e) {
                                    Log.e("GOOSE REQUEST", "Error parsing error response", e);
                                }
                            }

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
