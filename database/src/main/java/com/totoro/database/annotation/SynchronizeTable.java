package com.totoro.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同步表注解
 * Created by Chen on 2015/2/5.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SynchronizeTable {

    boolean synchronizable() default true;

    String syncName() default "";

    String idName();

    String deleteFlagName();
}
