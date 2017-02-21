package com.pickth.comepennyrenewal.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.main.MainActivity;

/**
 * Created by Kim on 2017-02-11.
 */

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            // 사용자 정보를 가져옴, 미가입시 자동 가입

            // 이메일 입력해야되는지 확인
            getUser();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }

            // 세션 연결 실패 시
            setContentView(R.layout.activity_login);
        }
    }

    protected void getUser() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectSignupActivity();
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode error = ErrorCode.valueOf(errorResult.getErrorCode());
                if (error == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    // 세션 연결 실패 시
                    setContentView(R.layout.activity_login);
                }
            }

            @Override
            public void onSuccess(UserProfile result) {
                if (!result.getProperties().get("email").toString().equals("")) {
                    redirectMainActivity();
                } else {
                    // 이메일 저장이 안돼있을 때
                    redirectSignupActivity();
                }
            }
        });
    }

    /**
     * 세션 연결 시 액티비티 이동
     */
    protected void redirectSignupActivity() {
        final Intent itSignup = new Intent(this, SignupEmailActivity.class);
        itSignup.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itSignup);
        finish();
    }

    protected void redirectMainActivity() {
        final Intent itMain = new Intent(this, MainActivity.class);
        itMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itMain);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
