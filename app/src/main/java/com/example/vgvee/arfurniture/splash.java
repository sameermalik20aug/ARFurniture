package com.example.vgvee.arfurniture;

import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(splash.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
        }
        },SPLASH_TIME_OUT);
        }
        }
