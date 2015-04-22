package com.totoro.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 同步字段
 * Created by Chen on 2015/3/26.
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface SynchronizeField {

    String syncName() default "";

    boolean stringType() default true;

    boolean upload() default true;
}
