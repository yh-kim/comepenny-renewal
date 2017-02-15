package com.pickth.comepennyrenewal.idea;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.net.service.BookService;
import com.pickth.comepennyrenewal.net.service.CommentService;
import com.pickth.comepennyrenewal.net.service.IdeaService;
import com.pickth.comepennyrenewal.util.DataManagement;
import com.pickth.comepennyrenewal.util.PickthDateFormat;
import com.pickth.comepennyrenewal.util.StaticUrl;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pickth.comepennyrenewal.R.id.btn_del;
import static com.pickth.comepennyrenewal.R.id.btn_pick;
import static com.pickth.comepennyrenewal.R.id.btn_share;
import static com.pickth.comepennyrenewal.R.id.tv_like;
import static com.pickth.comepennyrenewal.R.id.tv_time;
import static com.pickth.comepennyrenewal.R.id.tv_view;

/**
 * Created by Kim on 2017-02-06.
 */

public class IdeaHeaderViewHolder extends RecyclerView.ViewHolder {
    IdeaDetailActivity activity = (IdeaDetailActivity)itemView.getContext();
    boolean isBook = false;
    int isPick = 0;
    InputMethodManager keyboard;
    int ideaId = 0;
    IdeaService ideaService = new IdeaService();
    CommentService commentService = new CommentService();
    String userId = "";

    // book info
    String title = "";
    String author = "";
    String publisher = "";
    String image = "";
    String isbn = "none";


    String imgPath = image.split("\\?")[0];

    // screenshot
    File sdCardPath;
    FileOutputStream fos;
    String save;

    // bind view
    @BindView(btn_share)
    ImageButton btnShare;
    @BindView(R.id.btn_reple)
    TextView btnReple;
    @BindView(R.id.edit_reple)
    EditText editReple;
    @BindView(btn_pick)
    ImageButton btnPick;
    @BindView(btn_del)
    TextView btnDel;
    @BindView(R.id.layout_write_bg)
    LinearLayout layoutWriteBg;
    @BindView(R.id.logo_capture)
    ImageView logoCapture;
    @BindView(R.id.tv_idea_original)
    TextView tvIdeaOriginal;
    @BindView(tv_view)
    TextView tvView;
    @BindView(tv_like)
    TextView tvLike;
    @BindView(tv_time)
    TextView tvTime;
    @BindView(R.id.tv_comment_view)
    TextView tvCommentView;
    @BindView(R.id.tv_Writer)
    TextView tvWriter;
    @BindView(R.id.iv_boothicon)
    ImageView ivBoothIcon;

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

    public IdeaHeaderViewHolder(View itemView, Intent intent) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        //idea_id받기
        ideaId = intent.getExtras().getInt("idea_id");

        userId = DataManagement.getAppPreferences(itemView.getContext(), "user_id");

        //스크린키보드
        keyboard = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        initializationContent();

        initializeListener();
    }

    /**
     * 리스너 초기화
     */
    private void initializeListener() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenshot();
            }
        });

        btnReple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editReple.getText().toString().trim();

                if (content.length() == 0) {
                    Toast.makeText(itemView.getContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 서버에 저장
                postComment(userId, content);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"수정하기", "삭제하기"};

                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

                builder.setTitle("글을 수정/삭제 하시겠습니까?")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog1, int index) {
                                switch (index) {
                                    case 0:
                                        // 아이디어 수정 실행
                                        Intent itIdeaDetail = new Intent(activity.getApplicationContext(), ModifyIdeaActivity.class);
                                        itIdeaDetail.putExtra("idea_id", ideaId);
                                        String content = tvIdeaOriginal.getText().toString();
                                        itIdeaDetail.putExtra("content", content);
                                        itIdeaDetail.putExtra("isbn", isbn);
                                        if(isBook) {
                                            itIdeaDetail.putExtra("title", title);
                                            itIdeaDetail.putExtra("author", author);
                                            itIdeaDetail.putExtra("publisher", publisher);
                                            itIdeaDetail.putExtra("image", image);
                                        }
                                        activity.startActivityForResult(itIdeaDetail,1);
                                        activity.overridePendingTransition(0, 0);
                                        dialog1.cancel();
                                        break;
                                    case 1:
                                        // 아이디어 삭제
                                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                                        builder.setTitle("삭제 확인")        // 제목 설정
                                                .setMessage("이 글을 삭제하시겠습니까?")        // 메세지 설정
                                                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                    // 확인 버튼 클릭시 설정
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                      deleteIdea(ideaId);
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

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPick == 0) {
                    postLike(ideaId, userId);
                } else {
                    deleteLike(ideaId, userId);
                }
            }
        });

    }

    /**
     * 컨텐츠 사진으로 저장
     */
    private void screenshot() {
        btnShare.setClickable(false);
        btnPick.setClickable(false);

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
        logoCapture.setVisibility(View.VISIBLE);
        layoutWriteBg.setBackgroundResource(R.drawable.test1);
        //배경랜덤
//        int background[] = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4};
//        int idx = (int) (Math.random() * background.length);
//        layout_write_bg.setBackgroundResource(background[idx]);
        layoutWriteBg.setDrawingCacheEnabled(true);
        layoutWriteBg.buildDrawingCache(true);

        Bitmap captureView = Bitmap.createBitmap(layoutWriteBg.getMeasuredWidth(),
                layoutWriteBg.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas screenShotCanvas = new Canvas(captureView);
        layoutWriteBg.draw(screenShotCanvas);
        logoCapture.setVisibility(View.INVISIBLE);
        layoutWriteBg.setBackgroundResource(0);

        try {
            save = sdCardPath.getPath() + "/" + folder + "/" + 1 + ".jpg";
//            save = sdCardPath.getPath() + "/" + folder + "/" + idea_id + ".jpg";
            // 저장 경로
            fos = new FileOutputStream(save);
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 캡쳐
            layoutWriteBg.setDrawingCacheEnabled(false);

            File files = new File(save);
            if (files.exists() == true)  //파일유무확인
            {
                Uri uri = Uri.fromFile(new File(save));
                // 미디어 스캐너를 통해 모든 미디어 리스트를 갱신시킨다. // 미디어 스캐너를 통해 모든 미디어 리스트를 갱신시킨다.
                itemView.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                Intent intentSend = new Intent(Intent.ACTION_SEND);
                intentSend.setType("image/*");
                //이름으로 저장된 파일의 경로를 넣어서 공유하기
                intentSend.putExtra(Intent.EXTRA_STREAM, uri);
                itemView.getContext().startActivity(Intent.createChooser(intentSend, "공유하기")); //공유하기 창 띄우기
            }
            btnShare.setClickable(true);
            btnPick.setClickable(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 초기화 함수
     */
    public void initializationContent() {
        //초기화
        tvIdeaOriginal.setText("");
        getIdea(ideaId, userId);
        return;
    }


    /**
     * 액티비티 종료
     */
    public void finishActivity() {
        activity.finish();
    }


    /**
     * 아이디어 조회
     */
    public void getIdea(int ideaId, final String userId) {

        ideaService.getIdea(ideaId, userId)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jObject = new JSONObject(response.body().string());

                        int like = jObject.getInt("isLike");

                        JSONObject ideaObject = jObject.getJSONObject("idea");

                        String content = ideaObject.getString("content");
                        int ideaId = ideaObject.getInt("id");
                        int ideaUserId = ideaObject.getInt("userId");
                        String ideaEmail = ideaObject.getString("email");
                        int boothId = ideaObject.getInt("boothId");
                        int hit = ideaObject.getInt("hit");
                        int likeNum = ideaObject.getInt("likeNum");
                        int commentNum = ideaObject.getInt("commentNum");
                        isbn = ideaObject.getString("isbn");

                        String img_url = boothId + "";

                        //서버에서 date받아와서 formatTimeString이용해서 값 변환
                        String reg_Time = ideaObject.getString("date");
                        String time = PickthDateFormat.formatTimeString(reg_Time);

                        byte[] mailarray = ideaEmail.getBytes();
                        String email_view = new String(mailarray, 0, 3);
                        String hide_email = email_view + "*****";

                        tvWriter.setText(hide_email);
//                    tv_logo_name.setText(booth_name);
                        tvIdeaOriginal.setText(content);
                        tvView.setText(hit + "");
                        tvLike.setText(likeNum + "");
                        tvTime.setText(time);
                        tvCommentView.setText(commentNum + "");

                        Picasso.with(itemView.getContext())
                                .load(StaticUrl.FILE_URL + "/booth/" + boothId + ".png")
                                .fit()
                                .into(ivBoothIcon);

                        if (like == 1) {
                            isPick = 1;
                            btnPick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                        } else {
                            isPick = 0;
                            btnPick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                        }

                        // 글을 쓴 사람이거나 관리자이면
                        if (userId.equals(ideaUserId) || userId.equals("0")) {
                            btnDel.setVisibility(View.VISIBLE);
                        }

                        // 수정한 글일 때
                        if(activity.getResultCode() == 1) {
                            Intent backIntent = new Intent();
                            backIntent.putExtra("backContent", content);

                            activity.setBackIntent(backIntent);
                        }

                        // 등록한 책이 있다면
                        if(isbn != "none") {
                            isBook = true;
                            getBookInfo("d_isbn", isbn);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(response.code() == 404) {
                    // 삭제된 아이디어일 때
                    activity.setResultCode(2);
                    finishActivity();
                }else {
                    Toast.makeText(activity, response.code()+"error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    /**
     * 책 정보 조회
     */
    public void getBookInfo(String type, String value) {
        new BookService()
                .getBook(type, value)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jObject = new JSONObject(response.body().string());

                                JSONArray retArr = jObject.getJSONArray("items");
                                if(retArr.length() > 0) {
                                    JSONObject obj = retArr.getJSONObject(0);

                                    title = obj.getString("title");
                                    author = obj.getString("author");
                                    publisher = obj.getString("publisher");
                                    image = obj.getString("image");


                                    imgPath = image.split("\\?")[0];

                                    llBookInfo.setVisibility(View.VISIBLE);
                                    tvBookInfoTitle.setText(title);
                                    tvBookInfoAuthor.setText(author);
                                    tvBookInfoPublisher.setText(publisher);

                                    Picasso.with(itemView.getContext())
                                            .load(imgPath)
                                            .fit()
                                            .into(ivBookInfoImg);
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

    /**
     * 댓글 작성
     * @param content
     */
    public void postComment(String userId, String content) {
        commentService.postComment(ideaId, userId, content)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 201) {
                            editReple.setText("");

                            //키보드숨기기
                            keyboard.hideSoftInputFromWindow(itemView.getWindowToken(), 0);

                            // 새로고침
                            activity.initializeComment();
                        } else {
                            Toast.makeText(activity, response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(activity, "실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 좋아요
     */
    public void postLike(int ideaId, String userId) {
        ideaService.postLike(ideaId, userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 201) {
                            btnPick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                            isPick = 1;
                        } else {
                            Toast.makeText(itemView.getContext(), response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    /**
     * 좋아요 취소
     * @param ideaId
     * @param userId
     */
    public void deleteLike(int ideaId, String userId) {
        ideaService.deleteLike(ideaId, userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 204) {
                            btnPick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                            isPick = 0;
                        } else {
                            Toast.makeText(itemView.getContext(), response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    /**
     * 아이디어 삭제
     */
    public void deleteIdea(int ideaId) {
        ideaService.deleteIdea(ideaId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 204) {
                            activity.setResultCode(2);
                            finishActivity();
                        }
                        else {
                            Toast.makeText(activity, response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

}
