package com.totoro.commons.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Control ProgressDialog
 * Created by Chen on 2015/4/22.
 */
public class ProgressDialogUtil {

    protected ProgressDialog mDialog;
    protected Context context;

    public ProgressDialogUtil(Context context) {
        this.context = context;
    }

    public void showProgressDialog(int resId) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage(context.getString(resId));
            mDialog.setCancelable(false);
            mDialog.setIndeterminate(true);
            mDialog.show();
        } else {
            setProgressDialogMessage(resId);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }

    /**
     * Set ProgressDialog message
     */
    public void setProgressDialogMessage(int resId) {
        if (mDialog != null) {
            mDialog.setMessage(context.getString(resId));
        }
    }

    public void setProgressDialogMessage(String text) {
        if (mDialog != null) {
            mDialog.setMessage(text);
        }
    }

    /**
     * Dismiss ProgressDialog
     */
    public void dismissProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
