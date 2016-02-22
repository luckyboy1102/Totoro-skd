package com.totoro.commons.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Chen on 2015/4/22.
 */
public class ToastUtil {

    protected Toast toast = null;

    /**
     * 显示toast类
     * @param context
     * @param text
     */
    public void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(text);
            toast.show();
        }
    }

    /**
     * 显示toast类
     * @param context
     * @param resId
     */
    public void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }
}

