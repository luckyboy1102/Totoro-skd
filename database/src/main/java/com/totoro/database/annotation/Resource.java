package com.totoro.database.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 用于注入的注解
 * Created by Chen on 2015/2/5.
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface Resource {

    String name() default "";
}
