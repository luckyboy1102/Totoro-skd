package com.totoro.database;

import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.totoro.commons.Totoro;

import in.srain.cube.util.CLog;

/**
 * 数据库访问
 * Created by Chen on 15-3-23.
 */
public abstract class DatabaseManager {

    private static final String TAG = DatabaseManager.class.getSimpleName();

    private static DbUtils db;

    public DbUtils getDbUtils() {
        if (db == null) {
            DbUtils.DaoConfig daoConfig = new DbUtils.DaoConfig(Totoro.getInstance().getContext());
            daoConfig.setDbName(getDbName());
            daoConfig.setDbVersion(getDBVersion());
            daoConfig.setDbUpgradeListener(getUpgradeListener());

            db = DbUtils.create(daoConfig);
            db.configAllowTransaction(true);
            db.configDebug(debugModel());
        }
        return db;
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

    public abstract String getDbName();

    public abstract int getDBVersion();

    public abstract DbUtils.DbUpgradeListener getUpgradeListener();

    public abstract boolean debugModel();

}
