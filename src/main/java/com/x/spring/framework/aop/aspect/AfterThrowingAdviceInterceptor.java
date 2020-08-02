package com.x.spring.framework.aop.aspect;

import com.x.spring.framework.aop.intercept.MethodInterceptor;
import com.x.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yifanl
 * @Date 2020/8/1 17:17
 */
public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {


    private String throwingName;

    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Exception e){
            super.invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }
    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }

}
