package com.x.spring.framework.aop.aspect;

import com.x.spring.framework.aop.intercept.MethodInterceptor;
import com.x.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yifanl
 * @Date 2020/8/1 17:17
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }
}
