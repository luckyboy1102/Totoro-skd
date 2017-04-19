package com.example.dayasync;

import com.totoro.datasync.CursorType;
import com.totoro.datasync.annotation.SyncColumn;
import com.totoro.datasync.annotation.SyncTable;

/**
 * Created by chenshuai on 2017/4/14.
 */
@SyncTable(name = "PERSON", syncName = "PERSON", sequence = 0)
public class Person {

    @SyncColumn(name = "name", syncName = "NAME", rawType = CursorType.FIELD_TYPE_STRING)
    private String name;
    @SyncColumn(name = "id", syncName = "UID", rawType = CursorType.FIELD_TYPE_STRING)
    private String id;
    @SyncColumn(name = "gender", syncName = "SEX", rawType = CursorType.FIELD_TYPE_STRING)
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
