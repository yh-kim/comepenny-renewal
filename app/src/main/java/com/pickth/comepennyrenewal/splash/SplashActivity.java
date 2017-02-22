package com.pickth.comepennyrenewal.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.login.LoginActivity;
import com.pickth.comepennyrenewal.login.SignupEmailActivity;
import com.pickth.comepennyrenewal.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

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
                    sleep(400);
                    getUser();
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

    protected void getUser() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode error = ErrorCode.valueOf(errorResult.getErrorCode());
//                if(error == ErrorCode.CLIENT_ERROR_CODE) {
//                    finish();
//                } else {
//                    redirectLoginActivity();
//                }
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile result) {
                if(result.getThumbnailImagePath().equals("")) {
                    requestUpdateImagePath();
                }

                if(result.getProperties().get("email") != null){
                    redirectMainActivity();
                } else {
                    // 이메일 저장이 안돼있을 때
                    redirectSignupEmailActivity();
                }
            }
        });
    }

    protected void redirectLoginActivity() {
        final Intent itLogin = new Intent(this, LoginActivity.class);
        itLogin.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itLogin);
        finish();
    }

    protected void redirectMainActivity() {
        final Intent itMain = new Intent(this, MainActivity.class);
        itMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itMain);
        finish();
    }

    protected void redirectSignupEmailActivity() {
        final Intent itSignupEmail = new Intent(this, SignupEmailActivity.class);
        itSignupEmail.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itSignupEmail);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private void requestUpdateImagePath() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("thumbnail_image", "https://s3.ap-northeast-2.amazonaws.com/comepenny/myinfo_userimage.png");

        UserManagement.requestUpdateProfile(new SplashActivity.UsermgmtResponseCallback<Long>(){
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
            }
        }, properties);
    }

    private  abstract class UsermgmtResponseCallback<Long> extends ApiResponseCallback {

        @Override
        public void onSessionClosed(ErrorResult errorResult) {

        }

        @Override
        public void onNotSignedUp() {

        }

        @Override
        public void onSuccess(Object result) {

        }
    }
}
