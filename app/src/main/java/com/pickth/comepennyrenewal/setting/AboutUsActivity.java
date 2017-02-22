package com.pickth.comepennyrenewal.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-02-22.
 */

public class AboutUsActivity extends AppCompatActivity {
    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.tv_about_us)
    TextView tvAboutUs;

    String text = "\"우리가 할 수 있는 최선은\n" +
            "이 멋진 여행을 즐기는 것 뿐이다\"\n" +
            "\n" +
            "- 영화 '어바웃 타임'\n" +
            "\n" +
            "개발자 : 김미리, 김용훈, 이수연\n" +
            "디자이너 : 박지훈\n" +
            "기획자 : 이세민\n" +
            "- NOV. 14TH. SAT. \\\\`15";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);

        // actionbar
        {
            setSupportActionBar(mToolBar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            tvToolbar.setText("About Us");
        }

        tvAboutUs.setText(text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
