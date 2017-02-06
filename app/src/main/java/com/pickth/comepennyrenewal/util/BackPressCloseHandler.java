package com.pickth.comepennyrenewal.util;

import android.app.Activity;

/**
 * 두번 눌러 종료
 * Created by Kim on 2016-11-11.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
        }
    }
}
