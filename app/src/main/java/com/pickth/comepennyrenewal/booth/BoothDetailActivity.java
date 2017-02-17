package com.pickth.comepennyrenewal.booth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.idea.IdeaAdapter;
import com.pickth.comepennyrenewal.idea.IdeaDetailActivity;
import com.pickth.comepennyrenewal.idea.IdeaListItem;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.StaticNumber;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.pickth.comepennyrenewal.write.WriteActivity;
import com.squareup.picasso.Picasso;

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
 * Created by Kim on 2017-02-01.
 */

public class BoothDetailActivity extends AppCompatActivity {
    private boolean isScroll = false;
    private int count = StaticNumber.GET_IDEA_COUNT;
    private int offset = 0;
    int selectedItem = 0;

    View headerView;
    ArrayList<IdeaListItem> arrList = new ArrayList<IdeaListItem>(  );
    private int boothId = 0;
    private String boothName = "";
    IdeaAdapter adapter;
    LinearLayoutManager rvLayoutManager;

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_booth_detail_idea)
    RecyclerView rvBoothDetailIdea;

    //Initlist (초기화 메소드)
    public void initializeList() {
        //초기화
        isScroll = true;
        offset = 0;
        arrList.clear();

        getIdeaList();
        return;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booth_detail);
        ButterKnife.bind(this);

        //boothFragment에서 intent할때 보낸 값 받기
        Intent intent = getIntent();
        boothId = intent.getExtras().getInt("boothId");
        boothName = intent.getExtras().getString("boothName");

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

        // header view
        {
            headerView = getLayoutInflater().inflate(R.layout.header_booth_detail, null, false);
            Picasso.with(headerView.getContext())
                    .load(StaticUrl.FILE_URL+"/booth/"+boothId+".png")
                    .fit()
                    .into((ImageView)headerView.findViewById(R.id.img_booth));
        }

        // comment
        {
            rvLayoutManager = new LinearLayoutManager(getApplicationContext());
            adapter = new IdeaAdapter(arrList);
            adapter.setHeaderView(headerView);
            rvBoothDetailIdea.setLayoutManager(rvLayoutManager);
            rvBoothDetailIdea.setAdapter(adapter);

            rvBoothDetailIdea.setNestedScrollingEnabled(true);

            rvBoothDetailIdea.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int firstVisibleItem = rvLayoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = rvBoothDetailIdea.getChildCount();
                    int totalItemCount = rvLayoutManager.getItemCount();

                    if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) {
                        //서버로부터 받아온 List개수를 count
                        //지금까지 받아온 개수를 offset
                        if (count != 0 && offset > 3 && offset % StaticNumber.GET_IDEA_COUNT == 0) {
                            if (isScroll) {
                                //스크롤 멈추게 하는거
                                isScroll = false;
                                getIdeaList();
                            }
                        }
                    }
                }
            });

            adapter.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedItem = i - 1;
                    Intent itIdeaDetail = new Intent(getApplication(), IdeaDetailActivity.class);
                    itIdeaDetail.putExtra("idea_id", arrList.get(selectedItem).getId());
                    startActivityForResult(itIdeaDetail, 0);
                    overridePendingTransition(0,0);
                }
            });

            initializeList();
        }

        // fab
        {
            FloatingActionButton fab = ButterKnife.findById(this, R.id.booth_detail_fab);
            fab.setImageResource(R.drawable.btn_float2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itWrite = new Intent(getApplicationContext(), WriteActivity.class);
                    itWrite.putExtra("booth_id", boothId);
                    startActivityForResult(itWrite, 1);
                    overridePendingTransition(0, 0);
                }
            });
        }

        {
            initializeListener();
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (arrList.size() == 0) {
            initializeList();
            return;
        }

        // 추가버튼 눌렀을 때
        if(requestCode == 1) {
            initializeList();
            return;
        }

        switch (resultCode) {
            // 일반적 상황 (조회수, 좋아요수, 댓글수, 컨텐츠 업데이트), 수정했을 때
            case 0:
            case 1:
                String backContent = data.getStringExtra("backContent");
                int backView = data.getIntExtra("backView", 0);
                int backComment = data.getIntExtra("backComment", 0);
                int backLike = data.getIntExtra("backLike", 0);

                IdeaListItem backItem = arrList.get(selectedItem);
                backItem.setContent(backContent);
                backItem.setHit(backView);
                backItem.setCommentNum(backComment);
                backItem.setLikeNum(backLike);

                adapter.notifyDataSetChanged();
                break;

            // 삭제된 상황 (아이템 지우기)
            case 2:
                arrList.remove(selectedItem);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void initializeListener() {

    }

    private void getIdeaList() {
        new IdeaService().getIdeaListByBooth(boothId,offset)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("ret");
                                for (int i = 0; i < retArr.length(); i++) {
                                    JSONObject obj = retArr.getJSONObject(i);

                                    int ideaId = obj.getInt("id");
                                    int ideaUserId = obj.getInt("userId");
                                    int boothId = obj.getInt("boothId");
                                    String email = obj.getString("email");
                                    String content = obj.getString("content");
                                    int hit = obj.getInt("hit");
                                    String date = obj.getString("date");
                                    int likeNum = obj.getInt("likeNum");
                                    int commentNum = obj.getInt("commentNum");

                                    // Item 객체로 만들어야함
                                    IdeaListItem item = new IdeaListItem(content, email, boothName, hit, commentNum, likeNum, ideaId);
                                    // Item 객체를 ArrayList에 넣는다
                                    arrList.add(item);

                                    // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                                    adapter.notifyDataSetChanged();
//                                adapter.notifyItemChanged(i+offset+1);
                                }
                                count = jObject.getInt("cnt");
                                offset += count;
                                isScroll = true;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
