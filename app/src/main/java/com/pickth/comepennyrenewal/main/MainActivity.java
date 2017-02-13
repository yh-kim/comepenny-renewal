package com.pickth.comepennyrenewal.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.login.LoginActivity;
import com.pickth.comepennyrenewal.myinfo.MyInfoActivity;
import com.pickth.comepennyrenewal.setting.SettingActivity;
import com.pickth.comepennyrenewal.util.BackPressCloseHandler;
import com.pickth.comepennyrenewal.util.DataManagement;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    BackPressCloseHandler backPressCloseHandler;

    @BindView(R.id.main_draw_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.main_toolbar) Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 나중에 지워야됨
        DataManagement.setAppPreferences(this, "user_id", "0");

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
            }
        });

        {
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
                            break;
                        case R.id.nav_item_3:
                            // 설정
                            Intent itSetting = new Intent(getApplication(), SettingActivity.class);
                            startActivity(itSetting);
                            overridePendingTransition(0,0);
                            break;
                        case R.id.nav_item_4:
                            // 회원탈퇴
                            unLink();
                            break;
                    }
//                    mDrawerLayout.closeDrawers();
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
            ImageView customIdeaIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
            customIdeaIcon.setImageResource(R.drawable.selector_tab_homo);
            tabLayout.getTabAt(0).setCustomView(customIdeaIcon);

            ImageView customBoothIcon = (ImageView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
            customBoothIcon.setImageResource(R.drawable.selector_tab_category);
            tabLayout.getTabAt(1).setCustomView(customBoothIcon);

            // 처음에 선택돼 있게
            tabLayout.getTabAt(0).getCustomView().setSelected(true);
        }
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

    private void unLink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        redirectLoginActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }
}
