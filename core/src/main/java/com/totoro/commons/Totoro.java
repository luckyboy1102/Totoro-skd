package com.totoro.commons;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.totoro.commons.utils.ResourceUtil;

import in.srain.cube.diskcache.lru.SimpleDiskLruCache;
import in.srain.cube.util.CLog;
import in.srain.cube.util.CubeDebug;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.util.NetworkStatusManager;

/**
 * Created by Chen on 2015/4/22.
 */
public class Totoro {

    private static Totoro instance;

    private Application mApplication;

    public static Totoro onCreate(Application app) {
        instance = new Totoro(app);
        return instance;
    }

    private Totoro(Application application) {
        mApplication = application;

        // local display
        LocalDisplay.init(application);

        // network status
        NetworkStatusManager.init(application);

        // resource util
        ResourceUtil.init(application);
    }

    public void setDebug(boolean debug) {
        if (!debug) {
            CLog.setLogLevel(CLog.LEVEL_ERROR);
        } else {
            CLog.setLogLevel(CLog.LEVEL_VERBOSE);
            SimpleDiskLruCache.DEBUG = true;
            CubeDebug.DEBUG_CACHE = true;
            CubeDebug.DEBUG_IMAGE = true;
            CubeDebug.DEBUG_REQUEST = true;
        }
    }

    public static Totoro getInstance() {
        return instance;
    }

    public Context getContext() {
        return mApplication;
    }

    public String getAndroidId() {
        String id = Settings.Secure.getString(mApplication.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }
}
