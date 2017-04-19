package com.totoro.datasync.compiler;

import com.totoro.datasync.annotation.SyncColumn;
import com.totoro.datasync.annotation.SyncForeignColumn;
import com.totoro.datasync.annotation.SyncTable;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenshuai on 2017/4/18.
 */
public class EntityInfo {

    TypeElement element;
    SyncTable syncTable;
    Map<String, SyncColumn> syncColumns = new HashMap<String, SyncColumn>();
    List<SyncForeignColumn> syncForeignColumns = new ArrayList<SyncForeignColumn>();

    public EntityInfo(TypeElement element, SyncTable syncTable) {
        this.element = element;
        this.syncTable = syncTable;
    }

    public void addSyncColumn(SyncColumn column) {
        syncColumns.put(column.name(), column);
    }

    public void addSyncForeignColumn(SyncForeignColumn foreignColumn) {
        syncForeignColumns.add(foreignColumn);
    }
}
