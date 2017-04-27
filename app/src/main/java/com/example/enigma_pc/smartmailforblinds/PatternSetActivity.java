package com.example.enigma_pc.smartmailforblinds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.example.enigma_pc.smartmailforblinds.utility.Constants;

import java.util.List;

/**
 * Created by enigma-pc on 4/27/2017.
 */

public class PatternSetActivity extends Activity{
    int TotalTries=2;
    int triesCounter=0;
    String strPattern;
    PatternLockView mPatternLockView;
    String strPrevPattern;
    SharedPreferences pref;
    TextView tvSetPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_view_activity);
        tvSetPattern=(TextView)findViewById(R.id.tvSetPattern);
        tvSetPattern.setText("Set Pattern");
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.setNormalStateColor(ResourceUtils.getColor(this, R.color.colorPrimary));
        mPatternLockView.setPathEndAnimationDuration(2);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
    }

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            triesCounter++;
            if(triesCounter==1){
                Toast.makeText(getApplicationContext(),"Re-Draw pattern to Confrim",Toast.LENGTH_SHORT).show();
                strPrevPattern=PatternLockUtils.patternToString(mPatternLockView, pattern);
            }
            if(triesCounter==2){
                if(strPrevPattern.compareTo(PatternLockUtils.patternToString(mPatternLockView, pattern))==0){
                    SaveToPrefs(strPrevPattern);
                    Toast.makeText(getApplicationContext(),"Pattern is set",Toast.LENGTH_SHORT).show();
                    callPatternViewActivity();
                }else{
                    Toast.makeText(getApplicationContext(),"Re-Draw Pattern and confirm it",Toast.LENGTH_SHORT).show();
                    strPrevPattern="";
                    triesCounter=0;
                }

            }
            Toast.makeText(getApplicationContext(),PatternLockUtils.patternToString(mPatternLockView, pattern),Toast.LENGTH_SHORT).show();
            // first save this pattern in shared prefs to set pattern
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    public void SaveToPrefs(String strPattern){
        pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
        SharedPreferences.Editor editor;
        editor = pref.edit();
        editor.putBoolean(Constants.isLoggedIn, true); // Storing boolean - true/false
        editor.putString(Constants.pattern, strPattern);
        // Storing integer
        editor.commit();
    }
    public void callPatternViewActivity(){
        Intent intent=new Intent(PatternSetActivity.this,PatternViewActivity.class);
        startActivity(intent);
        PatternSetActivity.this.finish();
    }
}
