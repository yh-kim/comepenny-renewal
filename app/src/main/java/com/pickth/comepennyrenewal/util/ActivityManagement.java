package com.pickth.comepennyrenewal.util;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 닫히지 않은 액티비티 관리
 * Created by Kim on 2016-11-11.
 */

public class ActivityManagement {
    //onCreate되는 액티비티들 저장
    public static ArrayList<Activity> activityList = new ArrayList<Activity>();

    // 저장한 모든 액티비티 종료
    public void closeActivity(){
        for(int i = 0; i< activityList.size(); i++){
            activityList.get(i).finish();
        }
    }
}
