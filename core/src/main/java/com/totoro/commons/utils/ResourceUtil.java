package com.totoro.commons.utils;

import android.content.Context;

/**
 * Created by Chen on 2015/4/22.
 */
public class ResourceUtil {

    private static boolean sInitialed;
    private static Context mContext;

    public static void init(Context context) {
        if (sInitialed || context == null) {
            return;
        }
        sInitialed = true;
        mContext = context;
    }

    private static int getIdentifier(String paramString, String defType) {
        return mContext.getResources().getIdentifier(paramString, defType, mContext.getPackageName());
    }

    public static int getLayoutId(String paramString) {
        return getIdentifier(paramString, "layout");
    }

    public static int getStringId(String paramString) {
        return getIdentifier(paramString, "string");
    }

    public static int getDrawableId(String paramString) {
        return getIdentifier(paramString, "drawable");
    }

    public static int getStyleId(String paramString) {
        return getIdentifier(paramString, "style");
    }

    public static int getId(String paramString) {
        return getIdentifier(paramString, "id");
    }

    public static int getColorId(String paramString) {
        return getIdentifier(paramString, "color");
    }

    public static int getStyleableId(String paramString) {
        return getIdentifier(paramString, "styleable");
    }

    public static int getAnimId(String paramString) {
        return getIdentifier(paramString, "anim");
    }

    public static int getArrayId(String paramString) {
        return getIdentifier(paramString, "array");
    }

    public static String getString(String paramString) {
        return mContext.getString(getStringId(paramString));
    }
}
