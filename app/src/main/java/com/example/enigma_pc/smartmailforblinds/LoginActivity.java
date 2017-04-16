package com.example.enigma_pc.smartmailforblinds;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.enigma_pc.smartmailforblinds.Response.LoginResponse;
import com.example.enigma_pc.smartmailforblinds.Service.LoginService;
import com.example.enigma_pc.smartmailforblinds.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by enigma-pc on 3/1/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Observer {
    EditText etLoginEmailId;
    EditText etLoginPassword;
    Button btnLogin;
    TextView tvRegister;
    ConnectionDetector connectionDetector;
    LoginService loginService;
    ProgressDialog pDialog;
    SharedPreferences pref ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        etLoginEmailId=(EditText)findViewById(R.id.etLoginEmailId);
        etLoginPassword=(EditText)findViewById(R.id.etLoginPassword);

        tvRegister=(TextView)findViewById(R.id.tvRegister);

        btnLogin=(Button)findViewById(R.id.btnLogin);

        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        loginService=new LoginService();
        loginService.addObserver(this);
        pDialog= new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(" Wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);


    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.tvRegister){
            Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btnLogin){
            //call login service here
            CallRetrieveActivity();
            //loginUser();

        }

    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==loginService){
            if(!loginService.getResponse().getError()){
                Toast.makeText(getApplicationContext(),loginService.getResponse().getMsg().toString(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,ChooserActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),loginService.getResponse().getMsg().toString(),Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void loginUser(){
        if(etLoginEmailId.getText().toString().compareTo("")!=0
                && etLoginPassword.getText().toString().compareTo("")!=0){
            connectionDetector = new ConnectionDetector(getApplicationContext());
            if(connectionDetector.isConnectionToInternet()){
                loginCall();
               // loginString();
            }else{
                Toast.makeText(getApplicationContext(),"Connect To Internet",Toast.LENGTH_SHORT).show();
            }
           // loginService.login(getApplicationContext(),etLoginEmailId.getText().toString(),etLoginPassword.getText().toString());
        }else {
            Toast.makeText(getApplicationContext(),"Fill all fields",Toast.LENGTH_LONG).show();
        }



    }
//    public void login(){
//
//
//
//        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            //Process os success response
//                            if(response!=null){
//                                if(response.getString("msg").compareTo("Logged in")==0){
//                                    Toast.makeText(getApplicationContext(),"logged in Successfully",Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(getApplicationContext(),"Email or Password is incorrect",Toast.LENGTH_SHORT).show();
//                                }
//                            }else{
//                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
//            }
//        });
//        SmartSingletonClass.getInstance().addToRequestQueue(request_json);
//    }


    public void loginCall(){
        RequestQueue requestQueue = SmartSingletonClass.getInstance().getRequestQueue();
        pDialog.show();
                         //   http://smartmail.iproximity.co/api/v1/login
        final String URL =   "http://smartmail.iproximity.co/api/v1/login?email="+etLoginEmailId.getText().toString()+"&password="+etLoginPassword.getText().toString();
        Log.e("Url =",URL);
        //        url=url+
//// Post params to be sent to the server
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("email", etLoginEmailId.getText().toString());
//        params.put("password", etLoginPassword.getText().toString());


        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String UserId;

                if(pDialog!=null){
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }
                try {
                    if(response!=null){
                        if(response.getString("msg").compareTo("Logged in")==0){
                            Toast.makeText(getApplicationContext(),"logged in Successfully",Toast.LENGTH_SHORT).show();
                            JSONObject user=response.getJSONObject("data").getJSONObject("user");//getInt("user_id"));
                            String userId=String.valueOf(user.getInt("id"));
                            String name=user.getString("name");
                            String email=user.getString("email");

                            String auth_token=user.getString("auth_token");//response.getJSONObject("data").getString("auth_token").toString();
                            pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
                            SharedPreferences.Editor editor;
                            editor = pref.edit();
                            editor.putBoolean(Constants.isLoggedIn, true); // Storing boolean - true/false
                            editor.putString(Constants.UserId, userId);
                            editor.putString(Constants.name, name);
                            editor.putString(Constants.email, email);// Storing string
                            editor.putString(Constants.token, auth_token); // Storing integer
                            editor.commit();
                            //CallRetrieveActivity();

                        }else{
                            Toast.makeText(getApplicationContext(),"Email or Password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
//
//                    if(response!=null){
//
//                        UserId = response.getString(USER_ID);
//                        String success = response.getString(SUCCESS);
//                        String responseMessage = response
//                                .getString(RESPONSE_MESSAGE);
//                        if (success.equals("true")) {
//
//
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(Email, email.getText().toString());
//                            editor.putString(NAME, uname.getText().toString());
//                            editor.putString(CITY, cityName);
//                            editor.putString(MOBILE_NUMBER, mobile_no.getText().toString());
//                            editor.putString(COUNTRY, countryText.toString());
//                            editor.putString(USER_ID, UserId);
//                            editor.commit();
//                            Intent i = new Intent(SignUpActivity.this, Login.class);
//                            startActivity(i);
//                            SignUpActivity.this.finish();
//                            Log.e("Response Msg",responseMessage);
//                        } else {
//                            Log.e("Response Msg",responseMessage);
//                            Toast.makeText(SignUpActivity.this, "" + responseMessage, Toast.LENGTH_SHORT).show();
//                            pDialog.dismiss();
//                        }
//                    }else{
//
//                    }
                }catch (JSONException e){
                    Log.e("JsonException",e.getMessage().toString());
                }



            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pDialog!=null){
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }

                Log.e("Volley Error",error.toString());

            }


        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               // return super.getHeaders();
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");

                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void loginString(){

        RequestQueue requestQueue = SmartSingletonClass.getInstance().getRequestQueue();
        final String URL =   "http://smartmail.iproximity.co/api/v1/login?email="+etLoginEmailId.getText().toString()+"&password="+etLoginPassword.getText().toString();

       // url = "http://httpbin.org/post";
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", etLoginEmailId.getText().toString());
                params.put("password", etLoginPassword.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");

                return headers;

            }
        };
        requestQueue.add(postRequest);
    }

    public void CallRetrieveActivity(){
        Intent intent=new Intent(LoginActivity.this,RetrieveMailsFromGmail.class);
        startActivity(intent);
    }
}
