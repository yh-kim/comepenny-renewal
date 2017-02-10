package com.pickth.comepennyrenewal.idea;

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

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.DataManagement;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-02-10.
 */

public class ModifyIdeaActivity extends AppCompatActivity {
    int ideaId = 0;
    String userId = "";
    String content = "";

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.et_content)
    EditText etContent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ButterKnife.bind(this);

        // idea detail에서 intent할때 보낸 값 받기
        Intent intent = getIntent();
        ideaId = intent.getExtras().getInt("idea_id");
        content = intent.getExtras().getString("content");
        userId = DataManagement.getAppPreferences(this,"user_id");

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
            tvToolbar.setText("수정");
            if(content != null){
                etContent.setText(content);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                String content = etContent.getText().toString().trim();

                if (content.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 서버에 저장
                putIdea(ideaId, content);
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public void putIdea(int ideaId, String content) {
        new IdeaService()
                .putIdea(ideaId, content)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200) {
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
