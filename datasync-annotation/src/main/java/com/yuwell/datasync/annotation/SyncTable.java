package com.yuwell.datasync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenshuai on 2017/4/12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface SyncTable {

    int TYPE_DUPLEX = 0;
    int TPPE_UPLOAD = 1;
    int TYPE_DOWNLOAD = 2;

    String name();

    String syncName();

    int type() default TYPE_DUPLEX;

    int sequence() default -1;
}
