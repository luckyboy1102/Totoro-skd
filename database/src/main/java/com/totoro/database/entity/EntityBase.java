/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.totoro.database.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.totoro.database.annotation.SynchronizeField;

import java.util.Date;

/**
 * Author: wyouflf
 * Date: 13-8-13
 * Time: 上午11:15
 */
public abstract class EntityBase {

    public static final String NORMAL = "1";
    public static final String DELETED = "0";

    public static final String ID = "id";
    public static final String COLUMN_DELETE_FLAG = "deleteFlag";
    public static final String COLUMN_OPERATE_TIME = "operateTime";
    public static final String COLUMN_DEVICE_ID = "deviceId";
    public static final String COLUMN_ACCOUNT_ID = "accountId";

	// 主键
    private String id;

    // 删除标识
    @Column(column = "deleteFlag", defaultValue = NORMAL)
    private String deleteFlag;

    // 创建时间
    @Column(column = "operateTime")
    @SynchronizeField(syncName = "operatetime", stringType = false)
    private Date operateTime;

    // 操作设备
    @Column(column = "deviceId")
    @SynchronizeField(syncName = "deviceId", upload = false)
    private String deviceId;

    // 操作账户
    @Column(column = "accountId")
    private String accountId;

    public EntityBase() {}

    public EntityBase(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
