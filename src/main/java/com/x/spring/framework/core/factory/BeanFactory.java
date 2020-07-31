package com.x.spring.framework.core.factory;

/**
 * @author yifanl
 * @Classname BeanFactory
 * @Description TODO
 * @Date 2020/7/30 23:45
 * @Created by yifanli
 */
public interface BeanFactory {

    Object getBean(String name) throws Exception;

    <T> T getBean(Class<T> requiredType) throws Exception;

}
