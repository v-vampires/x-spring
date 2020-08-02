package com.x.spring.framework.aop;

/**
 * @author yifanl
 * @Date 2020/8/1 17:30
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
