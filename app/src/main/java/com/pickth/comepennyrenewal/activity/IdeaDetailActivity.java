package com.pickth.comepennyrenewal.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.adapter.CommentAdapter;
import com.pickth.comepennyrenewal.dto.CommentItem;
import com.pickth.comepennyrenewal.util.PickthDateFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pickth.comepennyrenewal.R.id.btn_pick;
import static com.pickth.comepennyrenewal.R.id.btn_share;

/**
 * Created by Kim on 2017-01-30.
 */

public class IdeaDetailActivity extends AppCompatActivity {
    ArrayList<CommentItem> arrList = new ArrayList<>();
    View headerView;
    InputMethodManager keyboard;
    int isPick = 0;

    // screenshot
    File sdCardPath;
    FileOutputStream fos;
    String save;

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.rv_idea_detail_comments)
    RecyclerView rvComments;

    HeaderViewHolder headerViewHolder = new HeaderViewHolder();
    public class HeaderViewHolder {
        @BindView(btn_share)
        ImageButton btnShare;
        @BindView(R.id.btn_reple)
        TextView btnReple;
        @BindView(R.id.edit_reple)
        EditText editReple;
        @BindView(btn_pick)
        ImageButton btnPick;
        @BindView(R.id.btn_del)
        TextView btnDel;
        @BindView(R.id.layout_write_bg)
        LinearLayout layoutWriteBg;
        @BindView(R.id.logo_capture)
        ImageView logoCapture;
    }

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

            //스크린키보드
            keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        // idea content
        {
            headerView = getLayoutInflater().inflate(R.layout.header_idea_detail, null, false);
            ButterKnife.bind(headerViewHolder, headerView);

            TextView tvWriter = (TextView)headerView.findViewById(R.id.tv_Writer);
            TextView tvTime = (TextView)headerView.findViewById(R.id.tv_time);
            tvWriter.setText("testestestsetㅇㅎ");
            tvTime.setText(PickthDateFormat.formatTimeString("2016-12-04 12:00:00"));
        }

        // comment
        {
            RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getApplicationContext());
            CommentAdapter adapter = new CommentAdapter(arrList);
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

        headerViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenshot();
            }
        });

        headerViewHolder.btnReple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = headerViewHolder.editReple.getText().toString().trim();

                if (content.length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 서버에 저장
                Toast.makeText(IdeaDetailActivity.this, "입력한값 : " + content, Toast.LENGTH_SHORT).show();

                //키보드숨기기
                keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        headerViewHolder.btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPick == 0) {
                    headerViewHolder.btnPick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                    isPick = 1;
                    // 서버 연결
                } else {
                    headerViewHolder.btnPick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                    isPick = 0;
                    // 서버 연결
                }
            }
        });

        headerViewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"수정하기", "삭제하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);

                builder.setTitle("글을 수정/삭제 하시겠습니까?")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog1, int index) {
                                switch (index) {
                                    case 0:
                                        // 아이디어 수정 실행
//                                        new NetworkIdeaAdjustWrite().execute();
                                        dialog1.cancel();
                                        break;
                                    case 1:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(IdeaDetailActivity.this);
                                        builder.setTitle("삭제 확인")        // 제목 설정
                                                .setMessage("이 글을 삭제하시겠습니까?")        // 메세지 설정
                                                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                    // 확인 버튼 클릭시 설정
                                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                                      new NetworkIdeaDel().execute();
                                                        dialog.cancel();
                                                    }
                                                })
                                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    // 취소 버튼 클릭시 설정
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                        dialog.show();    // 알림창 띄우기
                                        break;
                                    default:
                                        dialog1.cancel();
                                        break;
                                }

                            }
                        });
                AlertDialog dialog1 = builder.create();
                dialog1.show();

            }
        });
    }

    private void screenshot() {
        headerViewHolder.btnShare.setClickable(false);
        headerViewHolder.btnPick.setClickable(false);

        String folder = "HomoThinkus"; // 폴더 이름
        // 현재 날짜로 파일을 저장하기
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        // 년월일시분초
        //Date currentTime_1 = new Date();
        // String dateString = formatter.format(currentTime_1);
        sdCardPath = Environment.getExternalStorageDirectory();
        File dirs = new File(Environment.getExternalStorageDirectory(), folder);

        if (!dirs.exists()) { // 원하는 경로에 폴더가 있는지 확인
            dirs.mkdirs(); // Test 폴더 생성
            Log.d("CAMERA_TEST", "Directory Created");
        }
        //     int width_container = layout_write_bg.getWidth() ;//캡쳐할 레이아웃 크기
        //     int height_container = layout_write_bg.getHeight() ;//캡쳐할 레이아웃 크기
        headerViewHolder.logoCapture.setVisibility(View.VISIBLE);
        headerViewHolder.layoutWriteBg.setBackgroundResource(R.drawable.test1);
        //배경랜덤
//        int background[] = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4};
//        int idx = (int) (Math.random() * background.length);
//        layout_write_bg.setBackgroundResource(background[idx]);
        headerViewHolder.layoutWriteBg.setDrawingCacheEnabled(true);
        headerViewHolder.layoutWriteBg.buildDrawingCache(true);

        Bitmap captureView = Bitmap.createBitmap(headerViewHolder.layoutWriteBg.getMeasuredWidth(),
                headerViewHolder.layoutWriteBg.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas screenShotCanvas = new Canvas(captureView);
        headerViewHolder.layoutWriteBg.draw(screenShotCanvas);
        headerViewHolder.logoCapture.setVisibility(View.INVISIBLE);
        headerViewHolder.layoutWriteBg.setBackgroundResource(0);

        try {
            save = sdCardPath.getPath() + "/" + folder + "/" + 1 + ".jpg";
//            save = sdCardPath.getPath() + "/" + folder + "/" + idea_id + ".jpg";
            // 저장 경로
            fos = new FileOutputStream(save);
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 캡쳐
            headerViewHolder.layoutWriteBg.setDrawingCacheEnabled(false);

            File files = new File(save);
            if (files.exists() == true)  //파일유무확인
            {
                Uri uri = Uri.fromFile(new File(save));
                // 미디어 스캐너를 통해 모든 미디어 리스트를 갱신시킨다. // 미디어 스캐너를 통해 모든 미디어 리스트를 갱신시킨다.
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                Intent intentSend = new Intent(Intent.ACTION_SEND);
                intentSend.setType("image/*");
                //이름으로 저장된 파일의 경로를 넣어서 공유하기
                intentSend.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intentSend, "공유하기")); //공유하기 창 띄우기
            }
            headerViewHolder.btnShare.setClickable(true);
            headerViewHolder.btnPick.setClickable(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
