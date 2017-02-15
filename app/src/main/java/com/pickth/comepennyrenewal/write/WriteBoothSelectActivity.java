package com.pickth.comepennyrenewal.write;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.booth.BoothAdapter;
import com.pickth.comepennyrenewal.booth.BoothListItem;
import com.pickth.comepennyrenewal.net.service.BoothService;

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

public class WriteBoothSelectActivity extends AppCompatActivity {
    String sharedText = "";
    boolean selected=false;
    int boothId;
    BoothAdapter adapter;
    ArrayList<BoothListItem> arrList = new ArrayList<BoothListItem>();

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.gv_select_booth)
    GridView gvSelectBooth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_booth_select);
        ButterKnife.bind(this);

        /**
        // 로그인이 안 돼있는 유저라면
        String userId = DataUtil.getAppPreferences(getApplicationContext(), "user_id");
        if(userId.equals("")){
            Intent itMain = new Intent(WriteBoothActivity.this, LoginActivity.class);
            startActivity(itMain);
            overridePendingTransition(0, 0);
            finish();
        }
         **/

        // 인텐트를 얻어오고, 액션과 MIME 타입을 가져온다
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // 인텐트 정보가 있는 경우 실행
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);    // 가져온 인텐트의 텍스트 정보
            }
        }

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

        // Connect adapter
        {
            adapter = new BoothAdapter(WriteBoothSelectActivity.this, R.layout.row_booth, arrList);
            gvSelectBooth.setAdapter(adapter);
        }

        {
            getBoothList();
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
            case R.id.action_done:
                if(selected){
                    Intent company = new Intent(getApplicationContext(), WriteActivity.class);
                    company.putExtra("booth_Id", boothId);

                    // 공유 받은거라면
                    if(sharedText != ""){
                        company.putExtra("sharedText", sharedText);
                    }

                    startActivity(company);
                    overridePendingTransition(0, 0);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "카테고리를 선택해주세요",
                            Toast.LENGTH_SHORT).show();
                }
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

    private void initializeListener() {
        gvSelectBooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = true;
                boothId = arrList.get(position).getId();
                return;
            }
        });
    }

    private void getBoothList(){
        BoothService boothService = new BoothService();
        boothService.getBoothList().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jObject = new JSONObject(response.body().string());

                        JSONArray retArr = jObject.getJSONArray("ret");
                        for (int i = 0; i < retArr.length(); i++) {
                            JSONObject obj = retArr.getJSONObject(i);

                            int boothId = obj.getInt("id");
                            int ideaNum = obj.getInt("ideaNum");
                            int likeNum = obj.getInt("likeNum");
                            String imgUrl = boothId + "";
                            String boothName = obj.getString("name");

                            // Item 객체로 만들어야함
                            BoothListItem item = new BoothListItem(imgUrl, boothName, boothId, ideaNum, likeNum);
                            // Item 객체를 ArrayList에 넣는다
                            arrList.add(item);

                            // Adapter에게 데이터를 넣었으니 갱신하라고 알려줌
                            adapter.notifyDataSetChanged();
                        }

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
