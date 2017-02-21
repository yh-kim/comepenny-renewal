package com.pickth.comepennyrenewal.myinfo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.net.service.UserService;
import com.pickth.comepennyrenewal.util.DataManagement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kim on 2017-02-08.
 */

public class MyInfoActivity  extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    Uri mImageCaptureUri;
    Bitmap photo = null;
    String content = "";
    Uri uri;
    String userId,url;

    // Binding view
    @BindView(R.id.base_detail_toolbar)
    Toolbar mToolBar;

    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;

    @BindView(R.id.img_my_info_user)
    ImageView imgMyInfoUser;

    @BindView(R.id.tv_my_info_user_name)
    TextView tvMyInfoUserName;

    @BindView(R.id.tv_my_info_user_email)
    TextView tvMyInfoUserEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        ButterKnife.bind(this);

        userId = DataManagement.getAppPreferences(getApplicationContext(),"user_id");

        // 유저정보 가져오기
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                Map userInfo = result.getProperties();
                String email = userInfo.get("email").toString();
                String userNickName = result.getNickname();
                String userImage = result.getThumbnailImagePath();

                tvMyInfoUserName.setText(userNickName);
                tvMyInfoUserEmail.setText(email);
                Picasso.with(getApplicationContext())
                        .load(userImage)
                        .fit()
                        .into(imgMyInfoUser);
            }
        });

        // actionbar
        {
            setSupportActionBar(mToolBar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.btn_back);
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            tvToolbar.setText("내 정보");
        }

        // view pager
        {
            ViewPager viewPager = ButterKnife.findById(this,R.id.myinfo_viewpager);
            MyInfoFragmentAdapter adapter = new MyInfoFragmentAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            TabLayout tabLayout = ButterKnife.findById(this, R.id.myinfo_tab_layout);
            tabLayout.setupWithViewPager(viewPager);

            // tab icon 설정
            ImageView customIdeaIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
            customIdeaIcon.setImageResource(R.drawable.selector_tab_write);
            tabLayout.getTabAt(0).setCustomView(customIdeaIcon);

            ImageView customBoothIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
            customBoothIcon.setImageResource(R.drawable.selector_tab_like);
            tabLayout.getTabAt(1).setCustomView(customBoothIcon);

            // 처음에 선택돼 있게
            tabLayout.getTabAt(0).getCustomView().setSelected(true);
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


    //기본이미지 함수
    public void doBasePhotoAction() {
        try{
            File file = this.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File[] flist = file.listFiles();

            for(int i = 0 ; i < flist.length ; i++)
            {
                String fname = flist[i].getName();
                if(fname.equals(url))
                {
                    flist[i].delete();

                }

            }
            photo=null;
//            new NetworkImgdel().execute("");
            imgMyInfoUser.setImageBitmap(null);
        }catch(Exception e){
            //Toast.makeText(getApplicationContext(), "파일 삭제 실패 ", Toast.LENGTH_SHORT).show();
        }

    }


    //사진찍는 함수
    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mImageCaptureUri = createSaveCropFile();
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //앨범에서 가져오는 함수
    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //빈파일을 만들어서 위치를 알려줌(사진파일을 담을 파일)
    public Uri createSaveCropFile() {
        //경로
        //파일명 (현재시간의 밀리 세컨드값)
        //  String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        String url = userId + ".jpg";

        //파일 생성 -> 사진찍은걸 파일로 가지고있어야 전송할 수 있어
        //외장메모리 영역에 파일생성
        uri = Uri.fromFile(new File(this.getExternalFilesDir(Environment.DIRECTORY_DCIM), url));

        //파일 만든 위치를 uri
        return uri;
    }

    //결과 가져오는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //결과가 제대로 오지 않으면
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                File orignal_file = getImageFile(mImageCaptureUri);

                //이 파일을 카피하겠다
                mImageCaptureUri = createSaveCropFile();
                File copy_file = new File(mImageCaptureUri.getPath());

                copyFile(orignal_file, copy_file);
            }
            case PICK_FROM_CAMERA: {
                //카메라 앱이 꺼지면 그 사진을 자르기 할꺼야
                Intent intent = new Intent("com.android.camera.action.CROP");

                //(img경로 crop하라)
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // crop한 이미지를 저장할때 200x200 크기로 저장
//                intent.putExtra("outputX", 136); // crop한 이미지의 x축 크기
//                intent.putExtra("outputY", 136); // crop한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // crop 박스의 x축 비율
                intent.putExtra("aspectY", 1); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
                //crop한 output(img파일)을 다시 그 uri에 덮어씀
                intent.putExtra("output", mImageCaptureUri);

                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }
            case CROP_FROM_CAMERA: {
                //사진을 view시키는거
                //uri 주소를 String으로 가져옴
                String full_path = mImageCaptureUri.getPath();


                //"/" 를 기준으로 나누어 저장
                String[] s_path = full_path.split("/");

                //실제 사진경로만 뽑아옴
                int index = s_path[0].length() + 1;
                String photo_path = full_path.substring(index, full_path.length());

                photo = BitmapFactory.decodeFile(photo_path);

                putUserInfo();
//                new NetworkImgRegister().execute();

                //사진을 바로쓰지말고 bitmap으로 사이즈를 줄여서 처리하자
                BitmapFactory.Options options = new BitmapFactory.Options();
                for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
                    try {
                        photo = BitmapFactory.decodeFile(photo_path, options);
                        break;
                    } catch (OutOfMemoryError outOfMemoryError) {

                    }
                    //이미지뷰에 비트맵을 갖다넣는거야
                }
                imgMyInfoUser.setImageBitmap(photo);

                break;
            }
        }
    }

    public File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");

        if (mCursor == null || mCursor.getCount() < 1) {
            return null;
        }

        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            return false;
        }
        return result;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void putUserInfo() {
        String filePath = getApplicationContext().getFilesDir().getPath().toString() + "/photo_img.jpeg";
//        String filePath = "/photo_img.jpeg";
        File file = new File(filePath);

        FileOutputStream fos = null;
        try {
            //img 담는거
            if (photo != null) {

//                MediaType mediaType = MediaType.parse("image/jpeg");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                byte[] data = bos.toByteArray();

                fos = new FileOutputStream(file.getPath());
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MultipartBody.Part multipartFile = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"), file)
        );

        new UserService()
                .putUserInfo(multipartFile)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.code() == 200) {
                            JSONObject jObject = null;
                            try {
                                jObject = new JSONObject(response.body().string());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(MyInfoActivity.this, response.code()+"error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

//    @OnClick(R.id.img_my_info_user)
//    void click(View view) {
//        final CharSequence[] items = {"기본이미지", "사진앨범", "카메라"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);     // 여기서 this는 Activity의 this
//
//        // 여기서 부터는 알림창의 속성 설정
//        builder.setTitle("프로필 사진 설정").setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
//            public void onClick(DialogInterface dialog, int index) {
//                switch (index) {
//                    case 0:
//                        doBasePhotoAction();
//                        break;
//                    case 1:
//                        doTakeAlbumAction();
//                        break;
//                    case 2:
//                        doTakePhotoAction();
//                        break;
//                }
//            }
//        });
//        AlertDialog dialog = builder.create();    // 알림창 객체 생성
//        dialog.show();    // 알림창 띄우기
//    }
}
