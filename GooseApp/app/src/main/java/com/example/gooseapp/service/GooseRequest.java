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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gooseapp.sensors.ScannedBLEEntity;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GooseRequest {

    private static final String url= "https://192.168.1.225:5001";
    private static final String fingerprintUrl = "/api/fingerprint";
    private static final String bluetoothUrl = "/api/controlloBle";
    private static RequestQueue queue;
    private BackgroundService backgroundService;
    private SharedPreferences sharedPreferences;


    public GooseRequest(Context context, BackgroundService service){
        this.queue = Volley.newRequestQueue(context, new CustomSSLSocketFactory());
        this.backgroundService = service;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static void sendWifiScan(List<ScannedWifiEntity> swes, int userId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+fingerprintUrl,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                        Log.i("GOOSE RESPONSE", String.valueOf(response));
                        //TODO: capire se chiamare BLE oppure se mandare notifica
                        //todo: salva numero stanza come shared preferences
                        //per ble dovrei chiamare backgroundService.neededBLE()
                        /*
                        int value = (int) response;
                        switch (value) {
                            //NEED CHECK BLE
                            case 10:
                                break;
                            //AREA NOT RESTRICTED
                            case 21:
                                //todo: do nothing
                                break;
                            //AREA RESTRICTED
                            case 22:
                                //todo
                                break;
                            //NOT ALLOWED
                            case 23:
                                //todo
                                break;
                            //MISSING DEVICE
                            case 30:
                                //todo
                                break;
                            //EXIT MISSING DEVICE
                            case 31:
                                //todo
                                break;
                            //WRONG DEVICE
                            case 32:
                                //todo
                                break;
                            //HAS RIGHT DEVICES
                            case 33:
                                //todo
                                break;
                        }*/
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("GOOSE REQUEST", String.valueOf(error));
                    }
                }){
                    protected Map<String, String> getParams() {
                        Map<String, String > data = new HashMap<String, String>();
                        data.put("user_id", String.valueOf(userId));
                        for(ScannedWifiEntity swe : swes) {
                            data.putAll(swe.toStringMap());
                        }
                        return data;
                    }
        };
        queue.add(stringRequest);
    }


    public static void sendBLEScan(List<ScannedBLEEntity> sbes, int userId, int roomId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+bluetoothUrl,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                        Log.i("GOOSE RESPONSE", String.valueOf(response));
                        /*
                        int value = (int) response;
                        switch (value) {
                            //MISSING DEVICE
                            case 30:
                                //todo: backgroundService.sendBasicNotification("Missing Security Device", "Risultano ASSENTI dei dispositivi della sicurezza");
                                break;
                            //EXIT MISSING DEVICE
                            case 31:
                                //todo: backgroundService.sendBasicNotification("Exit Missing Security Device", "Stai uscendo dalla stanza senza avere tutti i dispositivi di sicurezza");
                                break;
                            //WRONG DEVICE
                            case 32:
                                //todo: backgroundService.sendBasicNotification("Wrong Device", "Dispositivi di sicurezza errati per la stanza");
                                break;
                            //HAS RIGHT DEVICES
                            case 33:
                                //todo: nothing
                                break;
                        }*/
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                }){
            protected Map<String, String> getParams() {
                Map<String, String > data = new HashMap<String, String>();
                data.put("room_id", String.valueOf(roomId));
                for(ScannedBLEEntity sbe : sbes) {
                    data.putAll(sbe.toStringMap());
                }
                return data;
            }
        };
        queue.add(stringRequest);
    }
}
