package com.hae.todoapp.utils;

import android.content.Context;
import android.util.Log;

import com.tashila.pleasewait.PleaseWaitDialog;

public class ProgressDialogLoadingUtils {
    private static PleaseWaitDialog mProgressDialog;
    private static final String TAG = "ProgressDialogLoadingUtils";
    public static void showProgressLoading(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new PleaseWaitDialog(context);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Log.d(TAG, "Progress show");
        }
    }

    public static void dismissProgressLoading() {
        if (mProgressDialog != null && mProgressDialog.getShowsDialog()) {
            mProgressDialog.dismiss();
            Log.d(TAG, "Progress dismiss");
        }
    }
}
