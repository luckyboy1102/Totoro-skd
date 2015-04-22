package com.totoro.commons.classresolver;

import android.content.pm.ApplicationInfo;

import com.totoro.commons.Totoro;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import in.srain.cube.util.CLog;

/**
 * 类解析工具
 * Created by Chen on 2015/2/5.
 */
public class ClassUtil {

    private static final String TAG = ClassUtil.class.getSimpleName();

    private static DexFile dexFile;

    private static PathClassLoader classLoader;

    static {
        ApplicationInfo applicationInfo = Totoro.getInstance().getContext().getApplicationInfo();
        classLoader = new PathClassLoader(applicationInfo.packageName, Thread.currentThread().getContextClassLoader());
        try {
            dexFile = new DexFile(applicationInfo.sourceDir);
        } catch (IOException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 扫描指定包名下的类
     * @param packageName
     * @return
     */
    public static List<Class> scanPackage(String packageName) {
        return scanPackage(packageName, new ClassFilter() {
            @Override
            public boolean accept(Class clazz) {
                return true;
            }
        });
    }

    /**
     * 扫描指定报名下的类，自定义过滤器
     * @param packageName
     * @param classFilter
     * @return
     */
    public static List<Class> scanPackage(String packageName, ClassFilter classFilter) {
        Map<String, Class> nameClassMap = scanPackageWithMapReturn(packageName, classFilter);
        return new ArrayList<Class>(nameClassMap.values());
    }

    public static Map<String, Class> scanPackageWithMapReturn(String packageName, ClassFilter classFilter) {
        Assert.assertNotNull(packageName);

        Class clazz;
        Map<String, Class> nameClassMap = new HashMap<String, Class>();
        Enumeration<String> entries = dexFile.entries();

        while (entries.hasMoreElements()) {
            String entry = entries.nextElement();
            if (entry.startsWith(packageName)) {
                try {
                    clazz = classLoader.loadClass(entry);
                    if (classFilter.accept(clazz)) {
                        nameClassMap.put(clazz.getSimpleName(), clazz);
                    }
                } catch (ClassNotFoundException e) {
                    CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
                }
            }
        }

        return nameClassMap;
    }

}
