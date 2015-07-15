package com.totoro.commons.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Control ProgressDialog
 * Created by Chen on 2015/4/22.
 */
public class ProgressDialogUtil {

    protected static ProgressDialog mDialog;

    public static void showProgressDialog(Context context, int resId) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage(context.getString(resId));
            mDialog.setCancelable(false);
            mDialog.setIndeterminate(true);
            mDialog.show();
        } else {
            setProgressDialogMessage(context, resId);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }

    /**
     * Set ProgressDialog message
     */
    public static void setProgressDialogMessage(Context context, int resId) {
        if (mDialog != null) {
            mDialog.setMessage(context.getString(resId));
        }
    }

    /**
     * Dismiss ProgressDialog
     */
    public static void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public static void recycle() {
        if (mDialog != null) {
            mDialog = null;
        }
    }
}
