package com.pickth.comepennyrenewal.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.main.MainActivity;
import com.pickth.comepennyrenewal.net.service.UserService;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-02-12.
 */

public class SignupEmailActivity extends AppCompatActivity {
    String email;
    String id = "";
    String profileImage = "";
    String thumbnailImage = "";
    String nickname = "";

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.et_signup_email)
    EditText etSignupEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email);
        ButterKnife.bind(this);

        // actionbar
        {
            setSupportActionBar(mToolBar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setDisplayShowHomeEnabled(true); // show or hide the default home button
            actionBar.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            actionBar.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        {
            tvToolbar.setText("이메일 입력");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                email = etSignupEmail.getText().toString().trim();

                if (email.length() == 0) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 정상적인 이메일인지 확인
                if(!isValidEmail(email)) {
                    // 실패하면
                    Toast.makeText(getApplicationContext(), "이메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 1. 서버에 저장
                UserManagement.requestMe(new MeResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {

                    }

                    @Override
                    public void onNotSignedUp() {

                    }

                    @Override
                    public void onSuccess(UserProfile result) {
                        id = String.valueOf(result.getId());
                        profileImage = result.getProfileImagePath();
                        thumbnailImage = result.getThumbnailImagePath();
                        nickname = result.getNickname();

                        postUser(id, email, profileImage, thumbnailImage, nickname);
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestUpdateEmail() {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("email", email);

        UserManagement.requestUpdateProfile(new UsermgmtResponseCallback<Long>(){
            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                redirectMainActivity();
            }
        }, properties);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    protected void redirectMainActivity() {
        final Intent itMain = new Intent(this, MainActivity.class);
        itMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itMain);
        finish();
    }

    private  abstract class UsermgmtResponseCallback<Long> extends ApiResponseCallback{

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

    private void postUser(String id, String email, String profileImage, String thumbnailImage, String nickname) {
        new UserService()
                .postUser(id, email, profileImage, thumbnailImage, nickname)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // 1. 이메일이 이미 있다면
                        // 에러
                        if(response.code() == 409) {
                            Toast.makeText(getApplicationContext(), "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 2. 정상적인 이메일 이라면
                        // 카카오 세션에 저장
                        if(response.code() == 201) {
                            requestUpdateEmail();
                        } else {
                            Toast.makeText(getApplicationContext(), response.code()+"error", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
