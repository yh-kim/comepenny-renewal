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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.book.BookFindActivity;
import com.pickth.comepennyrenewal.book.BookListItem;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.DataManagement;
import com.squareup.picasso.Picasso;

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
    boolean isBook = false;

    // book info
    String bookIsbn = "none";

    String bookTitle = "";
    String bookAuthor = "";
    String bookPublisher = "";
    String bookImgPath = "";

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.et_content)
    EditText etContent;

    // book info
    @BindView(R.id.ll_book_info)
    LinearLayout llBookInfo;
    @BindView(R.id.iv_book_info_img)
    ImageView ivBookInfoImg;
    @BindView(R.id.tv_book_info_title)
    TextView tvBookInfoTitle;
    @BindView(R.id.tv_book_info_author)
    TextView tvBookInfoAuthor;
    @BindView(R.id.tv_book_info_publisher)
    TextView tvBookInfoPublisher;
    @BindView(R.id.iv_add_book)
    ImageView ivAddBook;

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

        bookIsbn = intent.getExtras().getString("isbn");
        if(!bookIsbn.equals("none")) {
            // 책이 등록 되어있다면
            llBookInfo.setVisibility(View.VISIBLE);
            bookTitle = intent.getExtras().getString("title");
            bookAuthor = intent.getExtras().getString("author");
            bookPublisher = intent.getExtras().getString("publisher");
            bookImgPath = intent.getExtras().getString("image");

            tvBookInfoTitle.setText(bookTitle);
            tvBookInfoAuthor.setText(bookAuthor);
            tvBookInfoPublisher.setText(bookPublisher);

            Picasso.with(getApplicationContext())
                    .load(bookImgPath)
                    .fit()
                    .into(ivBookInfoImg);
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

        {
            tvToolbar.setText("수정");
            if(content != null){
                etContent.setText(content);
            }
        }

        {
            ivAddBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent itBookFInd = new Intent(getApplicationContext(), BookFindActivity.class);
                    startActivityForResult(itBookFInd, 1);
                    overridePendingTransition(0,0);
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
                String content = etContent.getText().toString().trim();

                if (content.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                // 서버에 저장
                putIdea(ideaId, content, new BookListItem(bookTitle, bookAuthor, bookPublisher, bookImgPath, bookIsbn));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            // bookFindActivity에서 확인을 누른 경우
            isBook = true;
            llBookInfo.setVisibility(View.VISIBLE);

            // 책정보 가져오는 함수
            bookTitle = data.getStringExtra("title");
            bookAuthor = data.getStringExtra("author");
            bookPublisher = data.getStringExtra("publisher");
            bookIsbn = data.getStringExtra("isbn");
            bookImgPath = data.getStringExtra("img_path");

            tvBookInfoTitle.setText(bookTitle);
            tvBookInfoAuthor.setText(bookAuthor);
            tvBookInfoPublisher.setText(bookPublisher);

            Picasso.with(getApplicationContext())
                    .load(bookImgPath)
                    .fit()
                    .into(ivBookInfoImg);

        }

    }

    public void putIdea(int ideaId, String content, BookListItem bookItem) {
        new IdeaService()
                .putIdea(ideaId, content, bookItem)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200) {
                            setResult(1);
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
