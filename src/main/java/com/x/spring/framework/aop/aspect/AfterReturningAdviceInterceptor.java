package com.x.spring.framework.aop.aspect;

import com.x.spring.framework.aop.intercept.MethodInterceptor;
import com.x.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yifanl
 * @Date 2020/8/1 17:17
 */
public class AfterReturningAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }



    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //先调用下一个拦截器
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        //再调用后置通知
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}
