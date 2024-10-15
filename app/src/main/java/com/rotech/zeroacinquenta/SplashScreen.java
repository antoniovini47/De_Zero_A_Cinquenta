package com.rotech.zeroacinquenta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class SplashScreen extends Activity {

    boolean stillSplash = true;

    protected void pulaSplash(){
        stillSplash=false;
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private final View.OnTouchListener pulasplash = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            pulaSplash();
            return false;
        }
    };

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        findViewById(R.id.imageSplash).setOnTouchListener(pulasplash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(stillSplash==true)
                    pulaSplash();
            }
        }, SPLASH_TIME_OUT);
    }
}
