package com.example.enigma_pc.smartmailforblinds;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.enigma_pc.smartmailforblinds.utility.Constants;

/**
 * Created by enigma-pc on 2/27/2017.
 */

public class SplashActiviy extends AppCompatActivity {
    SharedPreferences pref;
    String strPattern;
    boolean isSet=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
        strPattern = pref.getString(Constants.pattern, "");
        if(null==strPattern){
            isSet=false;
        }else if(strPattern.compareTo("")!=0){
            Toast.makeText(getApplicationContext(),strPattern,Toast.LENGTH_SHORT).show();
            isSet=true;
        }else if(strPattern.compareTo("")==0){

            isSet=false;
        }
        callActivity();
    }
    protected  void callActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isSet) {
                    Intent i = new Intent(SplashActiviy.this, PatternViewActivity.class);
                    startActivity(i);
                    SplashActiviy.this.finish();
                }else{
                    Intent i1 = new Intent(SplashActiviy.this, PatternSetActivity.class);
                    startActivity(i1);
                    SplashActiviy.this.finish();
                }
            }
        }, 5000);
    }
}
