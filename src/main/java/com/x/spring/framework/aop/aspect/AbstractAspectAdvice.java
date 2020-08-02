package com.x.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author yifanl
 * @Date 2020/8/1 17:08
 */
public abstract class AbstractAspectAdvice implements Advice {

    /**通知方法*/
    private Method aspectMethod;

    /**切面类*/
    private Object aspectTarget;

    public AbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if(parameterTypes == null || parameterTypes.length == 0){
            return this.aspectMethod.invoke(this.aspectTarget);
        }else{
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if(parameterTypes[i] == JoinPoint.class){
                    args[i] = joinPoint;
                }else if(parameterTypes[i] == Throwable.class){
                    args[i] = tx;
                }else if(parameterTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(this.aspectTarget, args);
        }
        
    }
}
