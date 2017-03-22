package com.example.enigma_pc.smartmailforblinds;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.enigma_pc.smartmailforblinds.Response.EmailModel;
import com.example.enigma_pc.smartmailforblinds.utility.Constants;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by enigma-pc on 2/27/2017.
 */

public class RetrieveMailsActivity extends AppCompatActivity  {
    //TODO implement watson engins tts here
    private ListView lvMails;
    SharedPreferences pref;
    private String userId;
    private ProgressDialog pDialog;
    ArrayList<EmailModel>emailsList;
    MyAdapter myAdapter;
    Button btnCallChoosser;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrievemail_activity);
        btnCallChoosser=(Button)findViewById(R.id.btnCallChoosser);
        btnCallChoosser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RetrieveMailsActivity.this,ChooserActivity.class);
                startActivity(intent);
            }
        });
        lvMails=(ListView)findViewById(R.id.lvMails);

        pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
        userId = pref.getString(Constants.UserId, "");
        emailsList=new ArrayList<EmailModel>();

        initializeDialogue();
        retrieveEmails();
        lvMails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EmailModel model= emailsList.get(position);
                Intent intent=new Intent(RetrieveMailsActivity.this,ReadMailActivity.class);
                intent.putExtra("model",model);
                startActivity(intent);
            }
        });



    }

    public URI getHost(String url){
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void retrieveEmails(){
        RequestQueue requestQueue = SmartSingletonClass.getInstance().getRequestQueue();
        pDialog.show();
        //   http://smartmail.iproximity.co/api/v1/login
        String URL =   "http://smartmail.iproximity.co/api/v1/email/show/"+userId;//2="+etFirstName.getText().toString()+"&email="+etEmailId.getText().toString()+"&password="+etPassword.getText().toString()+"&password_confirmation="+etRePassword.getText().toString();
        URL = URL.replaceAll(" ", "%20");
        Log.e("Url =",URL);
        //        url=url+
//// Post params to be sent to the server
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("email", etLoginEmailId.getText().toString());
//        params.put("password", etLoginPassword.getText().toString());


        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
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
                        if(emailsList!=null){
                            if(emailsList.size()>0){
                                emailsList.clear();
                            }
                        }

                  JSONArray emails=response.getJSONArray("data");
                        for(int i=0;i<emails.length();i++){
                            JSONObject jsonObject=emails.getJSONObject(i);
                            EmailModel model=new EmailModel();
                            model.setId(String.valueOf(jsonObject.getInt("id")));
                            model.setContent(jsonObject.getString("content"));
                            model.setSubject(jsonObject.getString("subject"));
                            model.setEmailTo(String.valueOf(jsonObject.getInt("to_id")));
                            model.setEmailFrom(String.valueOf(jsonObject.getInt("from_id")));
                            model.setCreatedAt(jsonObject.getString("created_at"));
                            emailsList.add(model);

                        }
                        setAdapter();
                       // ArrayList<MailModel>emailsList=new ArrayList<>();
//                        if(response.getString("msg").compareTo("User Registered")==0){
//
//                        }else{
//                            Toast.makeText(getApplicationContext(),response.getString("msg"),Toast.LENGTH_SHORT).show();
//                        }
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
        pDialog= new ProgressDialog(RetrieveMailsActivity.this);
        pDialog.setMessage(" Wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }
    public void setAdapter(){

        myAdapter=new MyAdapter(this,R.layout.view_email_list_row,emailsList);
        lvMails.setAdapter(myAdapter);

        /*simpleList.setAdapter(myAdapter);*/

    }








    public class MyAdapter extends ArrayAdapter<EmailModel> {

        ArrayList<EmailModel> emailList = new ArrayList<>();

        public MyAdapter(Context context, int textViewResourceId, ArrayList<EmailModel> objects) {
            super(context, textViewResourceId, objects);
            emailList = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.view_email_list_row, null);
            TextView tvSubject = (TextView) v.findViewById(R.id.tvSubject);
            TextView tvTo = (TextView) v.findViewById(R.id.tvTo);
            TextView tvFrom = (TextView) v.findViewById(R.id.tvFrom);
            TextView tvContent = (TextView) v.findViewById(R.id.tvContent);


            tvSubject.setText(emailsList.get(position).getSubject());
            tvTo.setText(emailsList.get(position).getEmailTo());
            tvFrom.setText(emailsList.get(position).getEmailFrom());
            tvContent.setText(emailsList.get(position).getContent());

            return v;

        }

    }







}
