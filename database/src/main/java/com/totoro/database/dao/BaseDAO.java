package com.totoro.database.dao;

import android.database.Cursor;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.CursorUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.totoro.commons.Totoro;
import com.totoro.database.QueryConst;
import com.totoro.database.entity.EntityBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import in.srain.cube.util.CLog;

public abstract class BaseDAO<T extends EntityBase> {
	
	public DbUtils db;
	private String TAG;
    Class<? extends EntityBase> entityClazz;

    @SuppressWarnings("unchecked")
	protected BaseDAO(DbUtils db, Class<? extends  EntityBase> entityClazz) {
		this.db = db;
        this.entityClazz = entityClazz;
        try {
            db.createTableIfNotExist(entityClazz);
        } catch (DbException e) {
            CLog.e(TAG, "create table " + entityClazz.getSimpleName() + " failed!", e.fillInStackTrace());
        }
    }
	
	public boolean saveOrUpdate(T t) {
		boolean flag = false;
        if (TextUtils.isEmpty(t.getId())) {
            t.setId(UUID.randomUUID().toString());
        }
        t.setDeviceId(Totoro.getInstance().getAppId());
        t.setOperateTime(new Date());
        try {
			db.saveOrUpdate(t);
			flag = true;
		} catch (DbException e) {
			CLog.e(TAG, "save or update failed!" + e.getMessage(), e.fillInStackTrace());
			flag = false;
		}
		return flag;
	}

    public boolean save(T t) {
        boolean flag = false;
        t.setId(UUID.randomUUID().toString());
        t.setDeviceId(Totoro.getInstance().getAppId());
        t.setOperateTime(new Date());
        try {
            db.save(t);
            flag = true;
        } catch (DbException e) {
            CLog.e(TAG, "save failed!" + e.getMessage(), e.fillInStackTrace());
        }
        return flag;
    }

    /**
     * 批量保存实体
     * @param list
     * @param transaction
     * @return
     */
    public boolean saveOrUpdateList(List<T> list, boolean transaction) {
        boolean flag = true;

        if (transaction) {
            db.configAllowTransaction(false);
            db.getDatabase().beginTransaction();
        }
        for (T t : list) {
            flag = saveOrUpdate(t) && flag;
        }
        if (transaction) {
            if (flag) {
                db.getDatabase().setTransactionSuccessful();
            }
            db.getDatabase().endTransaction();
            db.configAllowTransaction(true);
        }

        return flag;
    }

    /**
     * 批量保存实体，不开启事务
     * @param list
     * @return
     */
    public boolean saveOrUpdateList(List<T> list) {
        return saveOrUpdateList(list, true);
    }

    public T saveOrUpdateWithEntity(T t) {
        if (saveOrUpdate(t)) {
            return t;
        }
        return null;
    }

    public T getById(String id) {
        Selector selector = Selector.from(entityClazz).where(T.ID, "=", id);
        return getEntity(selector);
    }

    /**
     * 根据条件字段查询未删除的实体
     * @param condition
     * @return
     */
    public T getEntity(Map<String, Object> condition) {
        return getEntity(condition, true);
    }

    /**
     * 根据条件字段查询实体
     * @param condition
     * @return
     */
    public T getEntity(Map<String, Object> condition, boolean normal) {
        return getEntity(appendCondition(condition, normal));
    }

    /**
     * 查询列表(不含分页, 包含删除标识)
     * @param condition
     * @return
     */
    public List<T> getList(Map<String, Object> condition) {
        return getList(condition, true);
    }

    public List<T> getList(Map<String, Object> condition, boolean normal) {
        return getList(appendCondition(condition, normal));
    }

    /**
     * 查询列表（含分页）
     * @param selector
     * @param condition
     * @return
     */
	protected List<T> getList(Selector selector, Map<String, Object> condition) {
		Integer index = (Integer) condition.get(QueryConst.PAGE_INDEX);

		if (index != null) {
			selector.limit(QueryConst.PAGE_SIZE).offset(QueryConst.PAGE_SIZE * index);
		}

        return getList(selector);
    }

    /**
     * SQL语句直接查询
     * @param sql
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List getList(String sql) {
        long seq = CursorUtils.FindCacheSequence.getSeq();
        List data = null;
        Cursor cursor;
        try {
            cursor = db.execQuery(sql);
            if (cursor != null) {
                data = new ArrayList();
                while (cursor.moveToNext()) {
                    data.add(CursorUtils.getEntity(db, cursor, entityClazz, seq));
                }
            }
        } catch (DbException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return data == null ? Collections.emptyList() : data;
    }

    /**
     * 返回未删除的Selector
     * @return
     */
    protected Selector getNormalSelector() {
        return getSelector().where(EntityBase.COLUMN_DELETE_FLAG, "=", EntityBase.NORMAL);
    }

    /**
     * 返回普通Selector
     * @return
     */
    protected Selector getSelector() {
        return Selector.from(entityClazz);
    }

    protected Map<String, Object> emptyMap() {
        return Collections.<String, Object> emptyMap();
    }

    /**
     * 根据查询条件组装Selector对象（Selector含未删除标记）
     * @param condition
     * @return
     */
    protected Selector appendCondition(Map<String, Object> condition) {
        return appendCondition(condition, true);
    }

    protected Selector appendCondition(Map<String, Object> condition, boolean normal) {
        Selector selector = normal ? getNormalSelector() : getSelector();
        if (condition.size() > 0) {
            Iterator<Map.Entry<String, Object>> iterator = condition.entrySet().iterator();
            Map.Entry<String, Object> entry;

            if (!normal) {
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    if (QueryConst.PAGE_INDEX.equals(entry.getKey())) {
                        continue;
                    } else {
                        selector.where(entry.getKey(), "=", entry.getValue());
                        break;
                    }
                }
            }

            while (iterator.hasNext()) {
                entry = iterator.next();
                selector.and(entry.getKey(), "=", entry.getValue());
            }
        }
        return selector;
    }

    protected T getEntity(Selector selector) {
        T t = null;
        try {
            t = db.findFirst(selector);
        } catch (DbException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return t;
    }

    protected List<T> getList(Selector selector) {
        List<T> data = null;
        try {
            data = db.findAll(selector);
        } catch (DbException e) {
            CLog.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return data == null ? Collections.<T> emptyList() : data;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }
}
