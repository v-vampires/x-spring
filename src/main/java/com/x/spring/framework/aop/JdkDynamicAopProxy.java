package com.x.spring.framework.aop;

import com.x.spring.framework.aop.intercept.MethodInvocation;
import com.x.spring.framework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author yifanl
 * @Date 2020/8/1 17:34
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advisedSupport;

    public JdkDynamicAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advisedSupport.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advisedSupport.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取拦截链
        List<Object> interceptorsAndDynamicInterceptionAdvice = this.advisedSupport.getInterceptorsAndDynamicInterceptionAdvice(method, this.advisedSupport.getTargetClass());
        //外层拦截器，用于控制拦截器链的执行
        MethodInvocation invocation = new MethodInvocation(
                proxy,
                this.advisedSupport.getTarget(),
                method,
                args,
                this.advisedSupport.getTargetClass(),
                interceptorsAndDynamicInterceptionAdvice
        );
        return invocation.proceed();
    }
}
