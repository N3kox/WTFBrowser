package com.example.wtfbrowser.utils;

import android.content.Context;
import android.content.SharedPreferences;

//无图模式utils
public class SharedPreferencesUtils {

    public static final String KEY_NO_PIC_MODE = "no_pic_mode";

    public static SharedPreferences getSettingSP(Context context) {
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences("setting-sp", Context.MODE_PRIVATE);
    }


}
