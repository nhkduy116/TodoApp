package com.hae.todoapp.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToastShort(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }
    public static void showToastLong(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }
}
