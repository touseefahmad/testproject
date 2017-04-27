package com.example.enigma_pc.smartmailforblinds;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by enigma-pc on 2/27/2017.
 */

public class ChooserActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSendMail;
    private Button btnretrieveMail;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser_activity);

        btnSendMail=(Button)findViewById(R.id.btnSendMail);
        btnretrieveMail=(Button)findViewById(R.id.btnRetrieveMail) ;

        btnSendMail.setOnClickListener(this);
        btnretrieveMail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSendMail){
            sendMail();
        }
        if(v.getId()==R.id.btnRetrieveMail){
            retrieveMail();
        }


    }
    private void sendMail(){
       Intent intentS = new Intent(ChooserActivity.this,SendMailActivity.class);
        startActivity(intentS);
    }
    private void retrieveMail(){
        Intent intentR = new Intent(ChooserActivity.this,RetrieveMailsFromGmail.class);
        startActivity(intentR);
    }
}
