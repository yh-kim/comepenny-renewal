package com.pickth.comepennyrenewal.book;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.net.service.BookService;

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
 * Created by Kim on 2017-02-15.
 */

public class BookFindActivity extends AppCompatActivity {
    InputMethodManager keyboard;
    String isbn = "";
    String title = "";
    String author = "";
    String publisher = "";
    String imgPath = "";

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
    @BindView(R.id.et_book_name)
    EditText etBookName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_find);
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
            //스크린키보드
            keyboard = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

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

                    //키보드숨기기
                    keyboard.hideSoftInputFromWindow(etBookName.getWindowToken(), 0);

                    String bookName = etBookName.getText().toString();
                    getBookList("d_titl",bookName);
                }
            });

            lvBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    menu.getItem(0).setVisible(true);
                    isbn = arrList.get(position).getIsbn();
                    title = arrList.get(position).getTitle();
                    author = arrList.get(position).getAuthor();
                    publisher = arrList.get(position).getPublisher();
                    imgPath = arrList.get(position).getImage();
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
                Intent itBack = new Intent();
                itBack.putExtra("title", title);
                itBack.putExtra("author", author);
                itBack.putExtra("publisher", publisher);
                itBack.putExtra("isbn", isbn);
                itBack.putExtra("img_path", imgPath);

                setResult(1, itBack);
                finish();
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

    private void getBookList(String type, String value) {
        new BookService()
                .getBook(type, value)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("items");
                                for(int i=0; i<retArr.length(); i++) {
                                    JSONObject obj = retArr.getJSONObject(i);

                                    String title = obj.getString("title");
                                    String author = obj.getString("author");
                                    String publisher = obj.getString("publisher");
                                    String image = obj.getString("image");
                                    String isbn = obj.getString("isbn");


                                    String imgPath = image.split("\\?")[0];
                                    arrList.add(new BookListItem(title,author,publisher,imgPath,isbn));
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
