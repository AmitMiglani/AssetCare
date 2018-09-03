package com.toyota.assetcare.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.toyota.assetcare.R;
import com.toyota.assetcare.utils.CommonConstants;

public class SplashActivity extends Activity {

    Handler splashScreenHandler = new Handler();
    private long mDelayMillis = 3 * 1000;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        splashScreenHandler.postDelayed(splashScreenRunnable, mDelayMillis);
    }

    private Runnable splashScreenRunnable = new Runnable() {
        public void run() {
                if(prefs.getString(CommonConstants.EMAIL_SP, "").equals("")) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, AssetTypeActivity.class);
                    startActivity(intent);
                }
                finish();
        }
    };
}
