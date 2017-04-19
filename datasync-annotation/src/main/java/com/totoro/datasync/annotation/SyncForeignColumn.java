package com.totoro.datasync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by chenshuai on 2017/4/13.
 */
@Target(ElementType.FIELD)
@Retention(SOURCE)
public @interface SyncForeignColumn {

    String name();

    String syncName();

    int rawType();

    String foreignKey();

    String[] foreignColumns();

    String table();
}
