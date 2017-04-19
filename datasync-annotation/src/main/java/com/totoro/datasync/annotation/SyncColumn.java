package com.totoro.datasync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by chenshuai on 2017/4/12.
 */
@Target(ElementType.FIELD)
@Retention(SOURCE)
public @interface SyncColumn {

    String name();

    String syncName();

    int rawType();
}
