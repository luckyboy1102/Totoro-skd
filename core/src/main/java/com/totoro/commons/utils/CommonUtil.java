package com.totoro.commons.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 共同工具方法
 * Created by Chen on 2015/3/24.
 */
public class CommonUtil {

    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(17[0-9])|(15[^4,\\D])|(18[0-1,5-9]))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        Assert.assertNotNull(bitmap);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    /**
     * 保留一位小数
     * @param val
     * @return
     */
    public static String retainDecimal(String val, int decimalCount) {
        if (TextUtils.isEmpty(val)) {
            return "0";
        }

        if (val.endsWith(".")) {
            val = val.substring(0, val.length() - 1);
        }
        String[] strArr = val.split("\\.");
        if (decimalCount > 0) {
            if (strArr.length == 2 && strArr[1].length() > 1) {
                val = strArr[0] + "." + strArr[1].substring(0, decimalCount);
            }
        } else {
            val = strArr[0];
        }
        return val;
    }

    public static String multiply(float v1, float v2) {
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return retainDecimal(b1.multiply(b2).toString(), 1);
    }

}
