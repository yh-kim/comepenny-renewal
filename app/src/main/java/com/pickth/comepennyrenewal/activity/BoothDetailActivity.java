package com.pickth.comepennyrenewal.activity;

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
import com.pickth.comepennyrenewal.adapter.IdeaAdapter;
import com.pickth.comepennyrenewal.dto.IdeaListItem;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-02-01.
 */

public class BoothDetailActivity extends AppCompatActivity {
    View headerView;
    ArrayList<IdeaListItem> arrList = new ArrayList<IdeaListItem>(  );
    private int boothId = 0;
    private String boothName = "";

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_booth_detail_idea)
    RecyclerView rvBoothDetailIdea;

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
            RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
            IdeaAdapter adapter = new IdeaAdapter(arrList);
            adapter.setHeaderView(headerView);
            rvBoothDetailIdea.setLayoutManager(rvLayoutManager);
            rvBoothDetailIdea.setAdapter(adapter);

            adapter.onItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent itIdeaDetail = new Intent(getApplicationContext(), IdeaDetailActivity.class);
                    startActivity(itIdeaDetail);
                }
            });

            for(int i=0; i<12; i++){
                arrList.add(new IdeaListItem("content"+i, "email" +i, "부스 이름", 100*i, i, i*100, i));
            }
            adapter.notifyDataSetChanged();
        }

        // fab
        {
            FloatingActionButton fab = ButterKnife.findById(this, R.id.booth_detail_fab);
            fab.setImageResource(R.drawable.btn_float2);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itWriteBoothSelect = new Intent(getApplicationContext(), WriteBoothSelectActivity.class);
                    startActivity(itWriteBoothSelect);
                    overridePendingTransition(0,0);
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

    private void initializeListener() {

    }
}
