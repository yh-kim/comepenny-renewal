package com.pickth.comepennyrenewal.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.idea.MyInfoLikeActivity;
import com.pickth.comepennyrenewal.idea.MyInfoWriteActivity;
import com.pickth.comepennyrenewal.login.LoginActivity;
import com.pickth.comepennyrenewal.myinfo.MyInfoActivity;
import com.pickth.comepennyrenewal.setting.SettingActivity;
import com.pickth.comepennyrenewal.util.ActivityManagement;
import com.pickth.comepennyrenewal.util.BackPressCloseHandler;
import com.pickth.comepennyrenewal.util.DataManagement;
import com.pickth.comepennyrenewal.util.SetFont;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    BackPressCloseHandler backPressCloseHandler;
    View headerView;

    @BindView(R.id.main_draw_layout) DrawerLayout mDrawerLayout;

    @BindView(R.id.main_toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        {
            ActivityManagement.activityList.add(this);
            backPressCloseHandler = new BackPressCloseHandler(this);
        }

        // actionbar
        {
            setSupportActionBar(mToolBar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeAsUpIndicator(R.drawable.main_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setDisplayShowHomeEnabled(true); // show or hide the default home button
            actionBar.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            actionBar.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        // drawer
        {
            NavigationView navigationView = ButterKnife.findById(this,R.id.nav_view);
            headerView = navigationView.getHeaderView(0);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    item.setChecked(true);
                    switch (item.getItemId()) {
                        case R.id.nav_item_1:
                            // 내 정보
                            Intent itMyInfo = new Intent(getApplication(), MyInfoActivity.class);
                            startActivity(itMyInfo);
                            overridePendingTransition(0,0);

                            mDrawerLayout.closeDrawers();
                            break;
                        case R.id.nav_item_2:
                            // 내가 쓴 아이디어
                            startActivity(new Intent(getApplicationContext(), MyInfoWriteActivity.class));
                            overridePendingTransition(0,0);

                            mDrawerLayout.closeDrawers();
                            break;
                        case R.id.nav_item_3:
                            // Pick한 아이디어
                            startActivity(new Intent(getApplicationContext(), MyInfoLikeActivity.class));
                            overridePendingTransition(0,0);

                            mDrawerLayout.closeDrawers();
                            break;
                        case R.id.nav_item_4:
                            // 설정
                            Intent itSetting = new Intent(getApplication(), SettingActivity.class);
                            startActivity(itSetting);
                            overridePendingTransition(0,0);

                            mDrawerLayout.closeDrawers();
                            break;
                        case R.id.nav_item_5:
                            // 의견 보내기
                            sendEmail();
                            mDrawerLayout.closeDrawers();
                            break;
                    }
                    return true;
                }
            });
        }

        // view pager
        {
            ViewPager viewPager = ButterKnife.findById(this,R.id.main_viewpager);
            MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);

            TabLayout tabLayout = ButterKnife.findById(this, R.id.main_tab_layout);
            tabLayout.setupWithViewPager(viewPager);

            // tab icon 설정
            TextView customIdeaText = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_text_tab, null);
            customIdeaText.setText(getResources().getString(R.string.tab_item_1));
            customIdeaText.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans.ttf"));
            tabLayout.getTabAt(0).setCustomView(customIdeaText);

            TextView customBoothText = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_text_tab, null);
            customBoothText.setText(getResources().getString(R.string.tab_item_2));
            customBoothText.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans.ttf"));
            tabLayout.getTabAt(1).setCustomView(customBoothText);
//            ImageView customIdeaIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
//            customIdeaIcon.setImageResource(R.drawable.selector_tab_homo);
//            tabLayout.getTabAt(0).setCustomView(customIdeaIcon);
//
//            ImageView customBoothIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
//            customBoothIcon.setImageResource(R.drawable.selector_tab_category);
//            tabLayout.getTabAt(1).setCustomView(customBoothIcon);

            // 처음에 선택돼 있게
            tabLayout.getTabAt(0).getCustomView().setSelected(true);
        }

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
                String userId = String.valueOf(result.getId());
                String email = userInfo.get("email").toString();
                String userNickName = result.getNickname();
                String userImage = result.getThumbnailImagePath();

                DataManagement.setAppPreferences(getApplicationContext(), "user_id", userId);

                // navigation 셋팅
                setNavHeaderViewLayout(userNickName, email, userImage);
            }
        });
    }

    private void setNavHeaderViewLayout(String userName, String userEmail, String userImg) {
        LinearLayout llNavHeader = (LinearLayout)headerView.findViewById(R.id.ll_nav_header);

        int height = getHomeTouchButtonHeight();
        if(height != 0) {
            // 홈 터치 버튼이 있으면
            llNavHeader.setPadding(30,height*2/3,0,0);
        }

        ImageView ivNavUserImg = (ImageView)headerView.findViewById(R.id.iv_nav_user_img);
        TextView tvNavUserNickname = (TextView)headerView.findViewById(R.id.tv_nav_user_nickname);
        TextView tvNavUserEmail = (TextView)headerView.findViewById(R.id.tv_nav_user_email);

        tvNavUserNickname.setText(userName);
        tvNavUserEmail.setText(userEmail);
        Picasso.with(getApplicationContext())
                .load(userImg)
                .fit()
                .into(ivNavUserImg);
    }

    // 취소버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        // 네비게이션이 열려있으면
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        backPressCloseHandler.onBackPressed();
        Toast.makeText(getApplicationContext(), "한 번 더 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // 메뉴 버튼 눌렀을 때
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectLoginActivity() {
        final Intent itLogin = new Intent(this, LoginActivity.class);
        itLogin.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(itLogin);
        finish();
    }

    private void sendEmail() {
        Uri uri = Uri.parse("mailto:yonghoon.kim@pickth.com");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(it);
        overridePendingTransition(0,0);
    }

    private int getHomeTouchButtonHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
}
