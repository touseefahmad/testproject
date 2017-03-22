package com.example.enigma_pc.smartmailforblinds;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.enigma_pc.smartmailforblinds.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by enigma-pc on 2/28/2017.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final  String Prefs="MyPref";
    private EditText etFirstName;
   // private EditText etLastName;
    private EditText etEmailId;
    private  EditText etPassword;
    private EditText etRePassword;
    private Button btnRegister;
    private ProgressDialog pDialog;
    private ConnectionDetector connectionDetector;
    SharedPreferences pref ;
    SharedPreferences.Editor editor;// = pref.edit();//= getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initializeDialogue();
        etFirstName=(EditText)findViewById(R.id.etFirstName);
        etEmailId=(EditText)findViewById(R.id.etEmailId);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etRePassword=(EditText)findViewById(R.id.etRePassword);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        connectionDetector=new ConnectionDetector(getApplicationContext());
        pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRegister){
            if(etFirstName.getText().toString().compareTo("")!=0){
                if(etPassword.getText().toString().compareTo("")!=0){
                    if(etPassword.getText().toString().length()>5){
                        if(etPassword.getText().toString().compareTo(etRePassword.getText().toString())==0){
                            if(emailValidator(etEmailId.getText().toString())){
                                //TODO call register service
                                if(connectionDetector.isConnectionToInternet()){
                                    callRegisterService();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Connetct to Internet",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"Email is not valid",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"Password does'nt match",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Password is too small. It should contain atleast 6 characters or numbers",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Enter First Name",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void callRegisterService(){
        RequestQueue requestQueue = SmartSingletonClass.getInstance().getRequestQueue();
        pDialog.show();
        //   http://smartmail.iproximity.co/api/v1/login
        String URL =   "http://smartmail.iproximity.co/api/v1/register?name="+etFirstName.getText().toString()+"&email="+etEmailId.getText().toString()+"&password="+etPassword.getText().toString()+"&password_confirmation="+etRePassword.getText().toString();
        URL = URL.replaceAll(" ", "%20");
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
                        if(response.getString("msg").compareTo("User Registered")==0){
                            Toast.makeText(getApplicationContext(),"Registerd Succesfully",Toast.LENGTH_SHORT).show();
                            String userId=String.valueOf(response.getJSONObject("data").getInt("user_id"));
                            String auth_token=response.getJSONObject("data").getString("auth_token").toString();
                            editor = pref.edit();
                            editor.putBoolean("key_name", true); // Storing boolean - true/false
                            editor.putString(Constants.UserId, userId); // Storing string
                            editor.putString(Constants.token, auth_token); // Storing integer
                            editor.commit();
                        }else{
                            Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    }

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
    private void initializeDialogue(){
        pDialog= new ProgressDialog(RegisterActivity.this);
        pDialog.setMessage(" Wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
