package com.example.dayasync;

import com.totoro.datasync.CursorType;
import com.totoro.datasync.annotation.SyncColumn;
import com.totoro.datasync.annotation.SyncForeignColumn;
import com.totoro.datasync.annotation.SyncTable;

/**
 * Created by chenshuai on 2017/4/14.
 */
@SyncTable(name = "BPMEASURE", syncName = "BP", sequence = 1)
public class BPMeasure {

    @SyncColumn(name = "sbp", syncName = "SBP", rawType = CursorType.FIELD_TYPE_INTEGER)
    private int sbp;
    @SyncColumn(name = "dbp", syncName = "DBP", rawType = CursorType.FIELD_TYPE_INTEGER)
    private int dbp;
    @SyncColumn(name = "pr", syncName = "PR", rawType = CursorType.FIELD_TYPE_INTEGER)
    private int pulseRate;
    @SyncForeignColumn(name = "personId", syncName = "PERSONUID", rawType = CursorType.FIELD_TYPE_STRING,
            foreignKey = "id", table = "PERSON", foreignColumns = {"name", "gender"})
    private Person person;

    @SyncForeignColumn(name = "pointId", syncName = "POINTID", rawType = CursorType.FIELD_TYPE_STRING,
            foreignKey = "id", table = "point", foreignColumns = {"pointName"})
    private int point;

    public int getSbp() {
        return sbp;
    }

    public void setSbp(int sbp) {
        this.sbp = sbp;
    }

    public int getDbp() {
        return dbp;
    }

    public void setDbp(int dbp) {
        this.dbp = dbp;
    }

    public int getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(int pulseRate) {
        this.pulseRate = pulseRate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
