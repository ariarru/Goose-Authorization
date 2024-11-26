package com.example.gooseapp.service;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gooseapp.sensors.ScannedWifiEntity;

import java.util.HashMap;
import java.util.Map;

public class GooseRequest {

    private static final String url= "https://localhost:8080";
    private static final String fingerprintUrl = "https://localhost:8080/api/fingerprint";
    private static RequestQueue queue;

    public GooseRequest(Context context){
        this.queue = Volley.newRequestQueue(context);
    }

    public static void sendWifiScan(ScannedWifiEntity swe, int userId){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, fingerprintUrl,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i("GOOSE RESPONSE", "ho ottenuto una risposta dal backend:");
                        Log.i("GOOSE RESPONSE", String.valueOf(response));
                        //TODO: capire se chiamare BLE oppure se mandare notifica
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

                    }
                }){
                    protected Map<String, String> getParams() {
                        Map<String, String > data = new HashMap<String, String>();
                        data.put("user_id", String.valueOf(userId));
                        data.putAll(swe.toStringMap());
                        return data;
                    }
        };
        queue.add(stringRequest);
    }
}
