package com.pickth.comepennyrenewal.book;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kim on 2017-02-15.
 */

public class BookFindActivity extends AppCompatActivity {
    String isbn = "";
    int ideaId = 0;
    Menu menu;
    BookAdapter adapter;
    ArrayList<BookListItem> arrList = new ArrayList();

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.lv_book_list)
    ListView lvBookList;
    @BindView(R.id.iv_book_search)
    ImageView ivBookSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_find);
        ButterKnife.bind(this);

        // 아이디어 아이디 받기
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
            tvToolbar.setText("책 등록");
        }

        {
            adapter = new BookAdapter(getApplicationContext(), R.layout.row_book, arrList);
            lvBookList.setAdapter(adapter);
        }

        {
            ivBookSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    arrList.clear();
                    adapter.notifyDataSetChanged();
                    getBookList();
                }
            });

            lvBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    menu.getItem(0).setVisible(true);
                    isbn = arrList.get(position).getIsbn();
                    return;
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_done:
                postBook(ideaId, isbn);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.getItem(0).setVisible(false);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private void postBook(int ideaId, String isbn) {
        finish();
    }

    private void getBookList() {
        for(int i=0; i<5; i++) {
            arrList.add(new BookListItem("책제목"+i,"저자"+i,"출판사"+i,"http://bookthumb.phinf.naver.net/cover/104/625/10462578.jpg?type=m1&udate=20160404",""));
        }
        adapter.notifyDataSetChanged();
    }
}
