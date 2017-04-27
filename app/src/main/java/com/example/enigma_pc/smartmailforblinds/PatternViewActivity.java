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
 * Created by enigma-pc on 4/26/2017.
 */

public class PatternViewActivity extends Activity {

    PatternLockView mPatternLockView;
    SharedPreferences pref;
    String strPattern;
    TextView tvSetPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_view_activity);
        tvSetPattern=(TextView)findViewById(R.id.tvSetPattern);
        tvSetPattern.setText("Enter Pattern");
        pref=getApplicationContext().getSharedPreferences(RegisterActivity.Prefs, 0);
        strPattern = pref.getString(Constants.pattern, "");
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
            if(strPattern.compareTo(PatternLockUtils.patternToString(mPatternLockView, pattern))==0){
                callChooserActivity();
            }
            Toast.makeText(getApplicationContext(),PatternLockUtils.patternToString(mPatternLockView, pattern),Toast.LENGTH_SHORT).show();
            // first save this pattern in shared prefs to set pattern
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    public void callChooserActivity(){
        Intent intent=new Intent(PatternViewActivity.this,ChooserActivity.class);
        startActivity(intent);
        this.finish();
    }


}
