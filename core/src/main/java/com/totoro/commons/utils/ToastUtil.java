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
        if (toast == null) {
            toast = Toast.makeText(Totoro.getInstance().getContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(text);
            toast.show();
        }
    }

    /**
     * 显示toast类
     * @param resId
     */
    public void showToast(int resId) {
        showToast(Totoro.getInstance().getContext().getString(resId));
    }
}

