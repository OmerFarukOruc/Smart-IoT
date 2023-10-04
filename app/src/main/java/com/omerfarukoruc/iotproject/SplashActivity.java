package com.omerfarukoruc.iotproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    private static final String FIRST_START_KEY = "firstStart";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstStart = sharedPreferences.getBoolean(FIRST_START_KEY, true);

        if (isFirstStart)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    sharedPreferences.edit().putBoolean(FIRST_START_KEY, false).apply();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 3000);
        }

        else
        {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}
