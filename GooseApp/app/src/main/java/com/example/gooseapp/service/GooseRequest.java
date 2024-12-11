package com.example.gooseapp.service;

import static java.lang.Integer.parseInt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gooseapp.activity.MainActivity;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class GooseRequest {

    private static final String url= "https://172.20.10.3:5001";
            //"https://192.168.1.225:5001"; //casa "https://172.20.10.3:5001" //hotspot "192.168.1.19:5001"; //cla
    private static final String fingerprintUrl = "/api/fingerprint";
    private static final String bluetoothUrl = "/api/controlloBle";
    private static final String loginUrl = "/api/login";

    private static RequestQueue queue;
    private static BackgroundService backgroundService;
    private static MainActivity activity;
    
    // Add timeout constants
    private static final int SOCKET_TIMEOUT_MS = 30000; // 30 seconds timeout
    private static final int MAX_RETRIES = 2;

    public GooseRequest(Context context, MainActivity activity){
        this.queue = this.queue = Volley.newRequestQueue(context, new CustomSSLSocketFactory());
        this.activity = activity;
    }

    public GooseRequest(Context context, BackgroundService service){
        this.queue = Volley.newRequestQueue(context, new CustomSSLSocketFactory());

        this.backgroundService = service;
    }


    public void login(JSONObject body){
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url+loginUrl, body,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                            Log.i("GOOSE RESPONSE", response.toString());

                            if(response.length() > 0) {
                                activity.handleLoginResults(response);

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage =null;

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

                            if(errorMessage != null)
                                Log.e("GOOSE REQUEST", errorMessage);
                                JSONObject errorObj = new JSONObject();
                                try {
                                    errorObj.put("success", false);
                                    errorObj.put("error", errorMessage);
                                    activity.handleLoginResults(errorObj);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                        }
                    });
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    SOCKET_TIMEOUT_MS,
                    MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);


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
                            String errorMessage =null;
                            
                            // Check if the error has a network response with data
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject errorObj = new JSONObject(jsonError);
                                    if (errorObj.has("code")) {
                                        backgroundService.sendBasicNotification("NOT AUTHORIZED", "User not authorized to enter the room");

                                        //LATENZA: Memorizziamo il tempo di fine prima di inviare la notifica accesso non autorizzato
                                        long endTime = System.currentTimeMillis();
                                        //LATENZA: Calcola la latenza e Crea il dato di latenza come oggetto JSON
                                        long latency = endTime - startTime;
                                        //LATENZA: memorizza risultati
                                        Log.i("LATENZA WIFI", String.valueOf(latency));

                                        errorMessage = "User not authorized";
                                        return;
                                    }
                                } catch (JSONException e) {
                                    Log.e("GOOSE REQUEST", "Error parsing error response", e);
                                }
                            } else if (error instanceof TimeoutError) {
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

                            if(errorMessage != null)
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
                                String type = response.getString("notif_type");
                                Log.i("GOOSE RESPONSE", "Response value: " + responseValue);
                                /*
                                    AREA_NOT_RESTRICTED = 21 // no notifica
                                    MISSING_DEVICE = 30
                                    EXIT_MISSING_DEVICE = 31
                                    WRONG_DEVICE = 32
                                    HAS_RIGHT_DEVICES = 33 //no notifica
                                    EXTRA_DEVICES= 35 //no notifica
                                    * */

                                switch(responseValue){
                                    case 30 :
                                        switch (type){
                                                case "popup": backgroundService.sendBasicNotification("Missing Device", "You are in a restricted area and you do not have the necessary devices");
                                                break;
                                                case "lights": backgroundService.sendLightNotification("Missing Device", "You are in a restricted area and you do not have the necessary devices");
                                                    break;
                                                case "sound": backgroundService.sendSoundNotification("Missing Device", "You are in a restricted area and you do not have the necessary devices");
                                                    break;
                                        }
                                        break;
                                    case 32 : 
                                        switch (type){
                                            case "popup": backgroundService.sendBasicNotification("Wrong Device", "You are in a restricted area and have the wrong devices");
                                            break;
                                            case "lights": backgroundService.sendLightNotification("Wrong Device", "You are in a restricted area and have the wrong devices");
                                                break;
                                            case "sound": backgroundService.sendSoundNotification("Wrong Device", "You are in a restricted area and have the wrong devices");
                                                break;
                                        }
                                        break;
                                    case 31 : 
                                        switch (type){
                                            case "popup": backgroundService.sendBasicNotification("Missing Device", "you are leaving a restricted area without the security devices");
                                            break;
                                            case "lights": backgroundService.sendLightNotification("Missing Device", "you are leaving a restricted area without the security devices");
                                                break;
                                            case "sound": backgroundService.sendSoundNotification("Missing Device", "you are leaving a restricted area without the security devices");
                                                break;
                                        }
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
