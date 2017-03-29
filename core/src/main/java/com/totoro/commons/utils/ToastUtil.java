package com.totoro.commons.utils;

import android.widget.Toast;

import com.totoro.commons.Totoro;

/**
 * Created by Chen on 2015/4/22.
 */
public class ToastUtil {

    protected Toast toast = null;

    /**
     * 显示toast类
     * @param text
     */
    public void showToast(String text) {
        getToast(text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示toast类
     * @param resId
     */
    public void showToast(int resId) {
        getToast(resId, Toast.LENGTH_SHORT).show();
    }

    public Toast getToast(int resId, int duration) {
        if (toast == null) {
            toast = Toast.makeText(Totoro.getInstance().getContext(), resId, duration);
        } else {
            toast.setText(resId);
        }
        return toast;
    }

    public Toast getToast(String text, int duration) {
        if (toast == null) {
            toast = Toast.makeText(Totoro.getInstance().getContext(), text, duration);
        } else {
            toast.setText(text);
        }
        return toast;
    }
}

