package com.x.spring.framework.beans.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author yifanl
 * @Date 2020/7/31 22:00
 */
@Setter
@Getter
@NoArgsConstructor
public class BeanDefinition {
    /**
     * 保存实现类的全类名
     */
    private String beanClassName;

    private boolean lazyInit = false;
    /**
     * 保存实现类的类名（首字母小写），或者接口的全类名。 通过这个参数可以实现用类名或者接口类型来依赖注入
     */
    private String factoryBeanName;

}
