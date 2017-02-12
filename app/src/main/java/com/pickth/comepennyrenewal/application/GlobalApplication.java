package com.pickth.comepennyrenewal.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by Kim on 2017-02-12.
 */

public class GlobalApplication extends Application {
    private static volatile GlobalApplication instance = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return instance;
    }


    private static class KakaoSDKAdapter extends KakaoAdapter {

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }

        /**
         * default 값들이 존재하므로 필요한 상황에서만 사용하면 됨.
         * @return Session의 설정 값
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                /**
                 * 로그인 시 인증받을 타입을 지정한다. 지정하지 않을 시 가능한 모든 옵션이 지정
                 *
                 * 1. KAKAO_TALK
                 * 2. KAKAO_STORY
                 * 3. KAKAO_ACCOUNT : 웹뷰 DIalog를 통해 카카오 계정 연결
                 * 4. KAKAO_TALK_EXCLUDE_NATIVE_LOGIN : 카카오톡으로만 로그인을 유도하고 싶으면서 계정이 없을때 계정생성을 위한 버튼도 같이 제공을 하고 싶다면 지정.KAKAO_TALK과 중복 지정불가.
                 * 5. KAKAO_LOGIN_ALL : 모든 로그인 방식 사용
                 * @return
                 */
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[0];
                }

                /**
                 * SDK 로그인 시 사용되는 WebView에서 pause와 resume 시에 Timer를 설정하여 CPU 소모를 절약.
                 * true를 리턴할 경우 WebView로그인을 사용하는 화면에서 onPause, onResume에서 Timer 설정해야 됨
                 * 지정하지 않을 시 false로 설정됨
                 * @return
                 */
                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                /**
                 * 일반 사용자가 아닌 kakao와 제휴된 앱에서 사용되는 값.
                 * @return
                 */
                @Override
                public ApprovalType getApprovalType() {
                    return null;
                }

                /**
                 * Kakao SDK에서 사용하는 WebBiew에서 email 값을 저장할지 여부
                 * @return
                 */
                @Override
                public boolean isSaveFormData() {
                    return false;
                }
            };
        }
    }
}
