package com.x.spring.framework.aop.intercept;

/**
 * @author yifanl
 * @Date 2020/8/1 17:00
 * 拦截器接口
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
