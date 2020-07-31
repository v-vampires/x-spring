package com.x.spring.framework.context.support;

import com.x.spring.framework.core.factory.ApplicationContext;

/**
 * @author yifanl
 * @Date 2020/7/30 23:51
 */
public class DefaultApplicationContext implements ApplicationContext {
    /**
     * 配置文件路径
     */
    private String configLocation;

    public DefaultApplicationContext(String configLocation) {
        this.configLocation = configLocation;
        try {
            this.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() throws Exception{
        //1. 定位，定位配置文件
        //2. 加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        //3. 注册，把配置信息放到容器里面(伪IOC容器)
        //到这里为止，容器初始化完毕
        //4. 把不是延时加载的类，提前初始化
    }

    @Override
    public Object getBean(String name) throws Exception {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }
}
