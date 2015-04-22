package com.totoro.database;

import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.totoro.commons.Totoro;
import com.totoro.commons.classresolver.ClassFilter;
import com.totoro.commons.classresolver.ClassUtil;
import com.totoro.database.annotation.Resource;
import com.totoro.database.dao.BaseDAO;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import in.srain.cube.util.CLog;

/**
 * 数据库访问
 * Created by Chen on 15-3-23.
 */
public abstract class DatabaseManager {

    private static final String TAG = DatabaseManager.class.getSimpleName();

    private static DbUtils db;

    protected DatabaseManager(String packageNames) {
        initDb();
        inject(packageNames);
    }

    private void initDb() {
        if (db == null) {
            DbUtils.DaoConfig daoConfig = new DbUtils.DaoConfig(Totoro.getInstance().getContext());
            daoConfig.setDbName(getDbName());
            daoConfig.setDbVersion(getDBVersion());
            daoConfig.setDbUpgradeListener(getUpgradeListener());

            db = DbUtils.create(daoConfig);
            db.configAllowTransaction(true);
            db.configDebug(BuildConfig.DEBUG);
        }
    }

    private void inject(String packageNames) {
        // 扫描dao包下的类
        Map<String, Class> nameClassMap = ClassUtil.scanPackageWithMapReturn(packageNames, getDAOFilter());

        Class<? extends DatabaseManager> subClazz = getSubClazz();

        // DAO对象自动注入
        Field[] fields = subClazz.getDeclaredFields();
        Method setter;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Resource.class)) {
                Resource resourceAnnotation = field.getAnnotation(Resource.class);
                Class clazz = nameClassMap.get(resourceAnnotation.name());
                Assert.assertNotNull(clazz);

                try {
                    setter = subClazz.getMethod("set" + resourceAnnotation.name(), clazz);
                    Assert.assertNotNull(setter);
                    setter.invoke(this, clazz.getConstructor(DbUtils.class).newInstance(db));
                } catch (Exception e) {
                    CLog.e(TAG, "Inject dao field failed!", e.fillInStackTrace());
                    throw new RuntimeException("Initialize failed!");
                }
            }
        }
    }

    public boolean runRawSQL(String sql) {
        boolean success = false;
        try {
            db.execNonQuery(sql);
            success = true;
        } catch (DbException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return success;
    }

    public Cursor runRawSQLWithCursor(String sql) {
        Cursor cursor = null;
        try {
            cursor = db.execQuery(sql);
        } catch (DbException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return cursor;
    }

    protected void beginTransaction() {
        db.configAllowTransaction(false);
        db.getDatabase().beginTransaction();
    }

    protected void endTransaction() {
        db.getDatabase().endTransaction();
        db.configAllowTransaction(true);
    }

    protected void setTransactionSuccessful() {
        db.getDatabase().setTransactionSuccessful();
    }

    public abstract Class<? extends DatabaseManager> getSubClazz();

    public abstract String getDbName();

    public abstract ClassFilter getDAOFilter();

    public abstract int getDBVersion();

    public abstract DbUtils.DbUpgradeListener getUpgradeListener();

}
