package com.example.enigma_pc.smartmailforblinds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;


/**
 * Created by enigma-pc on 3/1/2017.
 */

public class ComposeMailActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etSendTo;
    TextView tvComposedText;
    Button btnComposeSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composemail_layout);

        etSendTo=(EditText)findViewById(R.id.etSendTo);
        tvComposedText=(TextView) findViewById(R.id.tvComposedText);
        btnComposeSend=(Button)findViewById(R.id.btnComposeSend);
        btnComposeSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnComposeSend){
            //call webservice
        }
    }






}



