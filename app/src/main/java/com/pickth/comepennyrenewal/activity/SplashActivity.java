package com.pickth.comepennyrenewal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pickth.comepennyrenewal.R;

/**
 * Created by Kim on 2017-01-13.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 화면이 보이고 2초(2000) 뒤에 로그인 페이지로 넘어감
        Thread mTimer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(300);
                    Intent intentSplash = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intentSplash);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
