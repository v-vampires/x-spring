package com.x.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author yifanl
 * @Classname JoinPoint
 * @Description TODO
 * @Date 2020/8/1 16:49
 * @Created by yifanli
 */
public interface JoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
