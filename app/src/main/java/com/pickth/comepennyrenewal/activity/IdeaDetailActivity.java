package com.pickth.comepennyrenewal.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.adapter.CommentAdapter;
import com.pickth.comepennyrenewal.dto.CommentItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-01-30.
 */

public class IdeaDetailActivity extends AppCompatActivity {
    ArrayList<CommentItem> arrList = new ArrayList<>();
    View headerView;

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_idea_detail_comments)
    RecyclerView rvComments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_detail);
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
//            //TextView 폰트 지정
//            SetFont.setGlobalFont(this, getWindow().getDecorView());

        }

        // idea content
        {
            headerView = getLayoutInflater().inflate(R.layout.header_idea_detail, null, false);
        }

        // comment
        {
            RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
            CommentAdapter adapter = new CommentAdapter(arrList, getIntent());
            adapter.setHeaderView(headerView);
            rvComments.setLayoutManager(rvLayoutManager);
            rvComments.setAdapter(adapter);

            for(int i=0; i<10; i++) {
                arrList.add(new CommentItem("", i+"댓글입니다.", "email", i+"oo", i));
            }
            adapter.notifyDataSetChanged();
        }

        // Initialize Listener
        {
            initializeListener();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // 메뉴 버튼 눌렀을 때
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeListener() {
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void finish() {
        // resultCode로 수정값이나 삭제한거 넘겨줘야 됨
        /**
         *
         *
         */
        super.finish();
        overridePendingTransition(0, 0);
    }
}
