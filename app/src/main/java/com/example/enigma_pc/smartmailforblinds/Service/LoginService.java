package com.example.enigma_pc.smartmailforblinds.Service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.enigma_pc.smartmailforblinds.DataObservor;
import com.example.enigma_pc.smartmailforblinds.GsonRequest;
import com.example.enigma_pc.smartmailforblinds.R;
import com.example.enigma_pc.smartmailforblinds.Response.LoginResponse;
import com.example.enigma_pc.smartmailforblinds.SmartSingletonClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by enigma-pc on 3/13/2017.
 */

public class LoginService extends DataObservor {

    LoginResponse response;

    public LoginResponse getResponse(){
        return response;
    }

    public void login(Context context,String email_id, String password){

        Map<String, String> params = new HashMap<String, String>();
        params.put("email_id", email_id);
        params.put("password", password);

        RequestQueue requestQueue = SmartSingletonClass.getInstance().getRequestQueue();
        GsonRequest<LoginResponse> request = new GsonRequest<>(
                Request.Method.POST, context.getResources()
                .getString(R.string.url) + "login.php?", LoginResponse.class, params,
                successListener(), errorListener());
        requestQueue.add(request);
    }


    private Response.Listener<LoginResponse> successListener() {
        // TODO Auto-generated method stub
        return new Response.Listener<LoginResponse>() {

            @Override
            public void onResponse(LoginResponse response) {
                // TODO Auto-generated method stub
                try{
                    LoginService.this.response=response;
                }catch(Exception e)
                {
                    LoginService.this.response=new LoginResponse();
                    LoginService.this.response.setError(true);
                    Log.d("Login Response", LoginService.this.getResponse().getMsg().toString());
                    LoginService.this.response.setMsg(e.getMessage());
                }
                triggerObservers();
            }
        };

    }

    private Response.ErrorListener errorListener() {
        // TODO Auto-generated method stub
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                LoginService.this.response= new LoginResponse();
                LoginService.this.response.setError(true);
              //  Log.d("LoginResponse", LoginService.this.getResponse().getMsg().toString());
                LoginService.this.response.setMsg(error.toString());
                triggerObservers();

            }
        };
    }
}
