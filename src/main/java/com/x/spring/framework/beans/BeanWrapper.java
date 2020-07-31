package com.x.spring.framework.beans;

/**
 * @author yifanl
 * @Date 2020/7/31 22:11
 * 用来封装通过读取BeanDefinition实例化后的实例
 */
public class BeanWrapper {

    private Object wrappedObject;

    public BeanWrapper(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedInstance(){
        return this.wrappedObject;
    }

    public Class<?> getWrappedClass(){
        return getWrappedInstance().getClass();
    }
}
