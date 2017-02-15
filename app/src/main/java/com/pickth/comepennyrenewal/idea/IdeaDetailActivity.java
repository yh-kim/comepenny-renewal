package com.pickth.comepennyrenewal.idea;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.comment.CommentAdapter;
import com.pickth.comepennyrenewal.comment.CommentItem;
import com.pickth.comepennyrenewal.net.service.CommentService;
import com.pickth.comepennyrenewal.util.PickthDateFormat;
import com.pickth.comepennyrenewal.util.SetFont;
import com.pickth.comepennyrenewal.util.StaticNumber;
import com.pickth.comepennyrenewal.util.StaticUrl;
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

    // dialog view
    ImageView ivRepleModifyBasic;
    EditText etRepleModifyText;
    TextView btnRepleUpdate, btnRepleCancel;
    AlertDialog mCommentModifyDialog;
    boolean isAdjustCheck = false;

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
            // 글 수정하고 왔을 때, 책 등록했을 때
            // 새로 고침
            View headerChangeView = getLayoutInflater().inflate(R.layout.header_idea_detail, null, false);
            adapter.setHeaderView(headerChangeView);
            adapter.notifyItemChanged(0);
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

        adapter.onItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i-1;
//                    user_email = DataUtil.getAppPreferences(getApplicationContext(), "user_email");

//                    if (user_email.equals(arr_list.get(commentDelPosition).getEmail())) {
                if(true){
                    final CharSequence[] items = {"댓글 수정하기", "댓글 삭제하기"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);     // 여기서 this는 Activity의 this

                    // 여기서 부터는 알림창의 속성 설정
                    builder.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                                public void onClick(DialogInterface dialog, int index) {
                                    // int형으로 조건 지정
                                    switch (index) {
                                        case 0:
                                            // 수정
                                            mCommentModifyDialog = createCommentModifyDialog();
                                            break;
                                        case 1:
                                            // 삭제
                                            AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);
                                            builder.setTitle("삭제 확인")
                                                    .setMessage("이 글을 삭제하시겠습니까?")        // 메세지 설정
                                                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                                // 확인 버튼 클릭시 설정
                                                                public void onClick(DialogInterface dialog_del, int whichButton) {
                                                                    deleteComment(arrList.get(selectedItem).getCommentId());
                                                                }
                                                            }
                                                    )
                                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                                // 취소 버튼 클릭시 설정
                                                                public void onClick(DialogInterface dialog_del, int whichButton) {
                                                                    dialog_del.cancel();
                                                                }
                                                            }
                                                    );
                                            AlertDialog dialog_del = builder.create();    // 알림창 객체 생성
                                            dialog_del.show();    // 알림창 띄우기
                                            break;
                                        default:
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            }
                    );

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();    // 알림창 띄우기
                }
                return false;
            }
        });
    }

    //dialog
    private AlertDialog createCommentModifyDialog() {
        final View innerView = getLayoutInflater().inflate(R.layout.dialog_comment, null);
        ivRepleModifyBasic = (ImageView) innerView.findViewById(R.id.iv_reple_modify_basic);
        etRepleModifyText = (EditText) innerView.findViewById(R.id.et_reple_modify_text);
        btnRepleUpdate = (TextView) innerView.findViewById(R.id.btn_reple_update);
        btnRepleCancel = (TextView) innerView.findViewById(R.id.btn_reple_cancel);


        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(innerView);
        //ab.setTitle("댓글 수정하기");
        ab.setCancelable(true);

        final Dialog mDialog = ab.create();

        Picasso.with(innerView.getContext())
                .load(StaticUrl.FILE_URL+arrList.get(selectedItem).getUserImg())
                .fit()
                .into(ivRepleModifyBasic);

        etRepleModifyText.setText(arrList.get(selectedItem).getContent());
        etRepleModifyText.setSelection(etRepleModifyText.length()); //커서를 끝에 위치!
        etRepleModifyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (arrList.get(selectedItem).getContent().equals(s.toString())) {
                    btnRepleUpdate.setVisibility(View.INVISIBLE);
                    isAdjustCheck = false;

                } else {
                    btnRepleUpdate.setVisibility(View.VISIBLE);
                    isAdjustCheck = true;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnRepleUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modifyContent = etRepleModifyText.getText().toString().trim();
                if (modifyContent.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isAdjustCheck) {
                    return;
                }

                putComment(arrList.get(selectedItem).getCommentId(), modifyContent);
                mDialog.cancel();

            }
        });
        btnRepleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        //dialog크기조절
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(mDialog.getWindow().getAttributes());
        // params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        // params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        //Window window = mDialog.getWindow();
        //window.setAttributes(params);
        return ab.create();

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

    private void putComment(int commentId, String comment) {
        Log.e("ttttttttt",commentId+"id, comment :"+comment);
        new CommentService()
                .putComment(commentId, comment)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        initializeComment();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    private void deleteComment(int commentId) {
        new CommentService()
                .deleteComment(commentId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        initializeComment();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
