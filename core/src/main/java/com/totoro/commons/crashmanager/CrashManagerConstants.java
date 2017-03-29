package com.totoro.commons.crashmanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * User: qii
 * Date: 13-3-21
 */
public class CrashManagerConstants {
    static String APP_VERSION = null;
    static String APP_PACKAGE = null;
    static String ANDROID_VERSION = null;
    static String PHONE_MODEL = null;
    static String PHONE_MANUFACTURER = null;
    static String CACHE_DIR = null;


    public static void loadFromContext(Context context) {
        CrashManagerConstants.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
        CrashManagerConstants.PHONE_MODEL = android.os.Build.MODEL;
        CrashManagerConstants.PHONE_MANUFACTURER = android.os.Build.MANUFACTURER;
        CrashManagerConstants.CACHE_DIR = getCacheDirPath(context);

        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            CrashManagerConstants.APP_VERSION = "" + packageInfo.versionCode;
            CrashManagerConstants.APP_PACKAGE = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getLogDir() {
        return CACHE_DIR;
    }

    private static String getCacheDirPath(Context context) {
        String path = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (context.getExternalCacheDir() != null) {
                path = context.getExternalCacheDir().getAbsolutePath();
            }
        } else {
            path = context.getCacheDir().getAbsolutePath();
        }

        return path;
    }
}
