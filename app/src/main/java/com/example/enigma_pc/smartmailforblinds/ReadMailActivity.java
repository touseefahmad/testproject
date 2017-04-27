package com.example.enigma_pc.smartmailforblinds;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enigma_pc.smartmailforblinds.Response.EmailModel;
import com.ibm.watson.developer_cloud.android.speech_common.v1.TokenProvider;
import com.ibm.watson.developer_cloud.android.text_to_speech.v1.TTSUtility;
import com.ibm.watson.developer_cloud.android.text_to_speech.v1.TextToSpeech;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by enigma-pc on 2/27/2017.
 */

public class ReadMailActivity extends AppCompatActivity{
    TextView tvMailFromR;
    TextView tvMailToR;
    TextView tvSubjectR;
    TextView tvContentR;
    EmailModel model;
    Button btnPlay;
    public Context mContext = null;
    public static JSONObject jsonVoices = null;
    private Handler mHandler = null;
    String strTo,strFrom,strSubject,strSnipet;
    boolean isBackPressed=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.readmail_activity);

        strTo= getIntent().getStringExtra("TO");//.getSerializableExtra("model");
        strFrom=getIntent().getStringExtra("FROM");
        strSubject=getIntent().getStringExtra("Subject");
        strSnipet=getIntent().getStringExtra("BODY");

        tvMailFromR=(TextView)findViewById(R.id.tvMailFromR);
        tvMailToR=(TextView)findViewById(R.id.tvMailToR);
        tvSubjectR=(TextView)findViewById(R.id.tvSubjectR);
        tvContentR=(TextView)findViewById(R.id.tvContentR);

        tvMailToR.setText(strTo);
        tvMailFromR.setText(strFrom);
        tvContentR.setText(strSnipet);
        tvSubjectR.setText(strSubject);
        btnPlay=(Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playTTS();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });



        if (initTTS() == false) {
            Toast.makeText(getApplicationContext(),"Error: no authentication credentials or token available, please enter your authentication information",Toast.LENGTH_SHORT).show();
           /* TextView viewPrompt = (TextView) mView.findViewById(R.id.prompt);
            viewPrompt.setText("Error: no authentication credentials or token available, please enter your authentication information");
            return mView;*/
        }else{
           /* TextToSpeech.sharedInstance().setVoice("en-US_AllisonVoice");

           // Sofia: North American Spanish (español norteamericano) female voice.
            TextToSpeech.sharedInstance().synthesize(tvContentR.getText().toString());
       */ }

        if (jsonVoices == null) {

            (new TTSCommands()).execute();
            if (jsonVoices == null) {
                Toast.makeText(getApplicationContext(),"Please, check internet connection.",Toast.LENGTH_SHORT).show();


            }
        }
       /* addItemsOnSpinnerVoices();
        updatePrompt(getString(R.string.voiceDefault));*/
        /*addItemsOnSpinnerVoices();
        updatePrompt(getString(R.string.voiceDefault));*/

        Spinner spinner = (Spinner) findViewById(R.id.spinnerVoices);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Log.d(TAG, "setOnItemSelectedListener");
                final Runnable runnableUi = new Runnable() {
                    @Override
                    public void run() {
                       updatePrompt(getSelectedVoice());
                    }
                };
                new Thread() {
                    public void run() {
                        mHandler.post(runnableUi);
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        mHandler = new Handler();





        }
    public void playTTS() throws JSONException {

        //TextToSpeech.sharedInstance().setVoice(fragmentTabTTS.getSelectedVoice());
        String voice=getSelectedVoice();
       // TextToSpeech.sharedInstance().setVoice("en-US_AllisonVoice");
        TextToSpeech.sharedInstance().setVoice(voice);
        String c="mail from "+ tvMailFromR.getText().toString() +" and subject is "+tvSubjectR.getText().toString()+" mail content is "+tvContentR.getText().toString();// "hjdg$h&jk8^i0ssh6";
        Pattern pt = Pattern.compile("[^a-zA-Z0-9_.-]");
        Matcher match= pt.matcher(c);
        while(match.find())
        {
            String s= match.group();
            c=c.replaceAll("\\"+s, "");
        }

        //Call the sdk function
        if(!isBackPressed){
        TextToSpeech.sharedInstance().synthesize(c);
        }else if(isBackPressed){
            isBackPressed=false;
            TextToSpeech.sharedInstance().synthesize("Stopping");
        }


    }








    static class MyTokenProvider implements TokenProvider {

        String m_strTokenFactoryURL = null;

        public MyTokenProvider(String strTokenFactoryURL) {
            m_strTokenFactoryURL = strTokenFactoryURL;
        }

        public String getToken() {

            Log.d(TAG, "attempting to get a token from: " + m_strTokenFactoryURL);
            try {
                // DISCLAIMER: the application developer should implement an authentication mechanism from the mobile app to the
                // server side app so the token factory in the server only provides tokens to authenticated clients
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(m_strTokenFactoryURL);
                HttpResponse executed = httpClient.execute(httpGet);
                InputStream is = executed.getEntity().getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(is, writer, "UTF-8");
                String strToken = writer.toString();
                Log.d(TAG, strToken);
                return strToken;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private boolean initTTS() {

        // DISCLAIMER: please enter your credentials or token factory in the lines below

        String username = getString(R.string.TTSUsername);
        String password = getString(R.string.TTSPassword);
        String tokenFactoryURL = getString(R.string.defaultTokenFactory);
        String serviceURL = "https://stream.watsonplatform.net/text-to-speech/api";

        TextToSpeech.sharedInstance().initWithContext(getHost(serviceURL));

        // token factory is the preferred authentication method (service credentials are not distributed in the client app)
        if (tokenFactoryURL.equals(getString(R.string.defaultTokenFactory)) == false) {
            TextToSpeech.sharedInstance().setTokenProvider(new MyTokenProvider(tokenFactoryURL));
        }
        // Basic Authentication
        else if (username.equals(getString(R.string.defaultUsername)) == false) {
            TextToSpeech.sharedInstance().setCredentials(username, password);
        } else {
            // no authentication method available
            return false;
        }
       // TextToSpeech.sharedInstance().setLearningOptOut(false); // Change to true to opt-out

        TextToSpeech.sharedInstance().setVoice(getString(R.string.voiceDefault));

        return true;
    }


    public URI getHost(String url){
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    public  class TTSCommands extends AsyncTask<Void, Void, JSONObject> {

        protected JSONObject doInBackground(Void... none) {
            jsonVoices=TextToSpeech.sharedInstance().getVoices();
            addItemsOnSpinnerVoices();
            updatePrompt(getString(R.string.voiceDefault));
            return TextToSpeech.sharedInstance().getVoices();
        }
    }
    public class ItemVoice {

        public JSONObject mObject = null;

        public ItemVoice(JSONObject object) {
            mObject = object;
        }

        public String toString() {
            try {
                return mObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void addItemsOnSpinnerVoices() {

        final Spinner spinner = (Spinner)findViewById(R.id.spinnerVoices);
        int iIndexDefault = 0;

        JSONObject obj = jsonVoices;
        ItemVoice [] items = null;
        try {
            JSONArray voices = obj.getJSONArray("voices");
            items = new ItemVoice[voices.length()];
            for (int i = 0; i < voices.length(); ++i) {
                items[i] = new ItemVoice(voices.getJSONObject(i));
                if (voices.getJSONObject(i).getString("name").equals(getString(R.string.voiceDefault))) {
                    iIndexDefault = i;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (items != null) {
            final ItemVoice[] data=items;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<ItemVoice> spinnerArrayAdapter = new ArrayAdapter<ItemVoice>(getApplicationContext(), android.R.layout.simple_spinner_item, data);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(0);
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        isBackPressed=true;
        try {
            playTTS();
        }catch (JSONException e){
            e.printStackTrace();
        }



        Intent intent=new Intent(ReadMailActivity.this,RetrieveMailsFromGmail.class);
        startActivity(intent);
        ReadMailActivity.this.finish();
       // super.onBackPressed();
    }

    // return the selected voice
    public String getSelectedVoice() {

        // return the selected voice
        Spinner spinner = (Spinner)findViewById(R.id.spinnerVoices);
        ItemVoice item = (ItemVoice)spinner.getSelectedItem();
        String strVoice = null;
        try {
            strVoice = item.mObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strVoice;
    }

    // update the prompt for the selected voice
    public void updatePrompt(final String strVoice) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

               final  TextView viewPrompt = (TextView)findViewById(R.id.prompt);
                if (strVoice.startsWith("en-US") || strVoice.startsWith("en-GB")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsEnglishPrompt));
                        }
                    });

                } else if (strVoice.startsWith("es-ES")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsSpanishPrompt));
                        }
                    });

                } else if (strVoice.startsWith("fr-FR")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsFrenchPrompt));
                        }
                    });

                } else if (strVoice.startsWith("it-IT")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsItalianPrompt));
                        }
                    });

                } else if (strVoice.startsWith("de-DE")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsGermanPrompt));
                        }
                    });

                } else if (strVoice.startsWith("ja-JP")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPrompt.setText(getString(R.string.ttsJapanesePrompt));
                        }
                    });

                }
            }
        });



    }



}
/***************************************************
 http://vidyasagarmsc.com/integrating-watson-text-speech-android-native-app/
 "name": "es-US_SofiaVoice",
 "language": "es-US",
 "customizable": true,
 "gender": "female",
 "url": "https://stream-s.watsonplatform.net/text-to-speech/api/v1/voices/es-US_SofiaVoice",
 "supported_features": {
 "voice_transformation": false,
 "custom_pronunciation": true
 },
 "description": "Sofia: North American Spanish (español norteamericano) female voice."


 */

/**********************************************
Sttc
 "url": "https://stream.watsonplatform.net/speech-to-text/api",
 "username": "689de348-ea10-4693-ae63-4c3aa1c0ce9d",
 "password": "jHNrhmqc5rwN"

 */