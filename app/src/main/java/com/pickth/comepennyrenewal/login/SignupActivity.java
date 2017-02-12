package com.pickth.comepennyrenewal.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.pickth.comepennyrenewal.main.MainActivity;

/**
 * Created by Kim on 2017-02-12.
 */

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUser();
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
                if(error == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSuccess(UserProfile result) {
                String kakaoID = String.valueOf(result.getId()); // userProfile에서 ID값을 가져옴
                String kakaoNickname = result.getNickname();     // Nickname 값을 가져옴
                redirectMainActivity();
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
}
