package com.pickth.comepennyrenewal.adapter.viewholder;

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
import com.pickth.comepennyrenewal.net.service.IdeaService;

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

import static com.pickth.comepennyrenewal.R.id.btn_pick;
import static com.pickth.comepennyrenewal.R.id.btn_share;

/**
 * Created by Kim on 2017-02-06.
 */

public class IdeaHeaderViewHolder extends RecyclerView.ViewHolder {
        int isPick = 0;
        InputMethodManager keyboard;
        int ideaId = 0;
        String email = "";
        IdeaService ideaService = new IdeaService();

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
        @BindView(R.id.btn_del)
        TextView btnDel;
        @BindView(R.id.layout_write_bg)
        LinearLayout layoutWriteBg;
        @BindView(R.id.logo_capture)
        ImageView logoCapture;
        @BindView(R.id.tv_idea_original)
        TextView tvIdeaOriginal;

        public IdeaHeaderViewHolder(View itemView, Intent intent) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //idea_id받기
//            ideaId = intent.getExtras().getInt("idea_id");
//            email = intent.getExtras().getString("email");

            //스크린키보드
            keyboard = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            initializeListener();
        }

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
                    Toast.makeText(itemView.getContext(), "입력한값 : " + content, Toast.LENGTH_SHORT).show();

                    //키보드숨기기
                    keyboard.hideSoftInputFromWindow(itemView.getWindowToken(), 0);
                }
            });

            btnPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPick == 0) {
                        btnPick.setBackgroundResource(R.drawable.detail_pickbutton_after);
                        isPick = 1;
                        // 서버 연결
                    } else {
                        btnPick.setBackgroundResource(R.drawable.detail_pickbutton_before);
                        isPick = 0;
                        // 서버 연결
                    }
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
//                                        new NetworkIdeaAdjustWrite().execute();
                                            dialog1.cancel();
                                            break;
                                        case 1:
                                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
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

        public void getIdea(){
            ideaService.getIdea("100","0").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jObject = new JSONObject(response.body().string());

                        JSONObject ideaObject = jObject.getJSONObject("idea");
                        String content = ideaObject.getString("content");

                        tvIdeaOriginal.setText(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

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
}
