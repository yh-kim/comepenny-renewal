package com.pickth.comepennyrenewal.idea;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.comment.CommentAdapter;
import com.pickth.comepennyrenewal.comment.CommentItem;
import com.pickth.comepennyrenewal.net.service.CommentService;
import com.pickth.comepennyrenewal.util.PickthDateFormat;
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticNumber;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-01-30.
 */

public class IdeaDetailActivity extends AppCompatActivity {
    private boolean isScroll = false;
    private int count = StaticNumber.GET_COMMENT_COUNT;
    private int offset = 0;
    int selectedItem = 0;

    int ideaId = 0;
    LinearLayoutManager rvLayoutManager;
    public ArrayList<CommentItem> arrList = new ArrayList<>();
    View headerView;
    public CommentAdapter adapter;
    private int resultCode = 0;
    private Intent backIntent = null;

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_idea_detail_comments)
    RecyclerView rvComments;

    public CommentAdapter getAdapter() {
        return adapter;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setBackIntent(Intent backIntent) {
        this.backIntent = backIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_detail);
        ButterKnife.bind(this);

        //idea_id받기
        Intent itReceive = getIntent();
        ideaId = itReceive.getExtras().getInt("idea_id");
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
            //TextView 폰트 지정
            SetFont.setGlobalFont(this, getWindow().getDecorView());

        }

        // idea content
        {
            headerView = getLayoutInflater().inflate(R.layout.header_idea_detail, null, false);
        }

        // comment
        {
            rvLayoutManager = new LinearLayoutManager(getApplicationContext());
            adapter = new CommentAdapter(arrList, getIntent());
            adapter.setHeaderView(headerView);
            rvComments.setLayoutManager(rvLayoutManager);
            rvComments.setNestedScrollingEnabled(false);
            rvComments.setAdapter(adapter);

        }

        {
            initializeComment();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            // 글 수정하고 왔을 때
            // 새로 고침
            View headerChangeView = getLayoutInflater().inflate(R.layout.header_idea_detail, null, false);
            adapter.setHeaderView(headerChangeView);
            adapter.notifyItemChanged(0);

//            TextView tvOrigin = (TextView)headerView.findViewById(R.id.tv_idea_original);
//            tvOrigin.setText("");
        }
    }

    public void initializeComment() {
        arrList.clear();
        offset = 0;
        isScroll = true;

        getCommentList();
    }

    private void initializeListener() {
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = rvLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = rvComments.getChildCount();
                int totalItemCount = rvLayoutManager.getItemCount();

                if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) {
                    if (count != 0 && offset > 3 && offset % StaticNumber.GET_COMMENT_COUNT == 0) {
                        if (isScroll) {
                            //스크롤 멈추게 하는거
                            isScroll = false;
                            getCommentList();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        // resultCode로 수정값이나 삭제한거 넘겨줘야 됨
        switch (resultCode) {
            case 1:
                setResult(resultCode, backIntent);
                break;
            case 0:
            case 2:
                setResult(resultCode);
                break;
        }
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void getCommentList() {
        new CommentService()
                .getCommentList(ideaId, offset)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("ret");
                                for (int index = 0; index < retArr.length(); index++) {
                                    JSONObject obj = retArr.getJSONObject(index);

                                    int commentId = obj.getInt("id");
                                    String comment = obj.getString("comment");
//                                String email = obj.getString("email");
                                    String userId = obj.getString("userId");

//                                user_comment_img = obj.getString("image_t");


                                    //서버에서 date받아와서 formatTimeString이용해서 값 변환
                                    String regTime = obj.getString("date");
                                    String date = PickthDateFormat.formatTimeString(regTime);


                                    // Item 객체로 만들어야함
                                    CommentItem items = new CommentItem("/booth/1.png", comment, userId + "@test.com", date, commentId);

                                    arrList.add(0, items);
                                    adapter.notifyDataSetChanged();
                                }

                                count = jObject.getInt("cnt");
                                offset += count;
                                isScroll = true;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(IdeaDetailActivity.this, response.code()+"error", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
