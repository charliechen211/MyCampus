package com.stpi.campus.util.navigation.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/8/16.
 */


public class ToastUtil {

    public static void show(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    }
}
