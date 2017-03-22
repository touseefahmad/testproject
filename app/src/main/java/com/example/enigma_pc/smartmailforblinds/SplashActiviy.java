package com.example.enigma_pc.smartmailforblinds;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by enigma-pc on 2/27/2017.
 */

public class SplashActiviy extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        callActivity();
    }
    protected  void callActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i=new Intent(SplashActiviy.this,LoginActivity.class);
                startActivity(i);
            }
        }, 5000);
    }
}
