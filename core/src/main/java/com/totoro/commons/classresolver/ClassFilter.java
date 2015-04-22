package com.totoro.commons.classresolver;

/**
 * 类过滤器接口
 * Created by Chen on 2015/2/5.
 */
public interface ClassFilter {

    public boolean accept(Class clazz);
}
