package com.pickth.comepennyrenewal.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 데이터 저장, 호출
 * Created by Kim on 2016-11-11.
 */

public class DataManagement {
    //파일에서 데이터 가져오는거
    public static String getAppPreferences(Context context, String key){
        String returnValue = null;
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("pickth",0);
        returnValue = pref.getString(key,"");
        return returnValue;
    }

    //파일에 저장하는거
    public static void setAppPreferences(Context context, String key, String value){
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("pickth",0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();
    }
}
