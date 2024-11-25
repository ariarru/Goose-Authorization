package com.example.gooseapp.service;

import static java.lang.Integer.parseInt;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GooseRequest {

    private final String url= "https://localhost:8080";
    private RequestQueue queue;
    private BackgroundService service = new BackgroundService();

    public GooseRequest(Context context){
        this.queue = Volley.newRequestQueue(context);
    }

    public void sendRequest(){
        StringRequest stringRequest
                = new StringRequest(
                Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        int value = (int) response;
                        switch (value) {
                            //NEED CHECK BLE
                            case 10:
                                service.backgroundMeasureBLE();
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
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                });
        requestQueue.add(stringRequest);
    }
}
