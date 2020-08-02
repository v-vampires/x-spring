package com.x.spring.framework.context.support;

import com.x.spring.framework.annotation.Autowired;
import com.x.spring.framework.aop.AopProxy;
import com.x.spring.framework.aop.CglibAopProxy;
import com.x.spring.framework.aop.JdkDynamicAopProxy;
import com.x.spring.framework.aop.config.AopConfig;
import com.x.spring.framework.aop.support.AdvisedSupport;
import com.x.spring.framework.beans.BeanWrapper;
import com.x.spring.framework.beans.config.BeanDefinition;
import com.x.spring.framework.beans.support.BeanDefinitionReader;
import com.x.spring.framework.context.ApplicationContext;
import com.x.spring.framework.core.factory.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yifanl
 * @Date 2020/7/30 23:51
 * IOC容器初始化总结：
 * 1. 找到配置文件，封装成Properties
 * 2. 读取Properties中的扫描路径变量，扫描该路径下的class并保存到集合中
 * 3. 读取上一步扫描好的class集合，封装成BeanDefinition集合
 * 4. 将BeanDefinition集合注册到容器中（Map<String, BeanDefinition>）
 *
 */
public class DefaultApplicationContext implements ApplicationContext {
    /**
     * 配置文件路径
     */
    private String configLocation;

    private BeanDefinitionReader reader;
    //保存factoryBean和BeanDefinition的对应关系
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

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
        reader = new BeanDefinitionReader(this.configLocation);
        //2. 加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3. 注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);
        //到这里为止，容器初始化完毕
        //4. 把不是延时加载的类，提前初始化
        doAutoWired();
    }

    private void doAutoWired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if(beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The \"" + beanDefinition.getFactoryBeanName() + "\" is exists!!");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     *
     * @param beanName
     * @return
     * @throws Exception
     * 核心逻辑：
     * 1. 如果已经实例化了，则直接获取实例化后的对象即可。如果没有实例化则走后面的逻辑
     * 2. 拿到该bean的BeanDefinition，通过反射实例化
     * 3. 将实例化后的对象封装到BeanWrapper中
     * 4. 将封装好的BeanWrapper保存到IOC容器（实际就是一个map中）
     * 5. 依赖注入实例化bean
     * 6. 返回最终实例
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        Object instance = getSingleton(beanName);
        if(instance != null){
            return instance;
        }
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //1.调用反射初始化Bean
        instance = instantiateBean(beanName, beanDefinition);
        //2.把这个对象封装到BeanWrapper中
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //3.把BeanWrapper保存到IOC容器中去
        //注册一个类名（首字母小写，如helloService）
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        //注册一个全类名（如com.x.app.HelloService）
        this.factoryBeanInstanceCache.put(beanDefinition.getBeanClassName(), beanWrapper);
        //4.注入
        populateBean(beanName, new BeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }



    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            AdvisedSupport config = getAopConfig();
            config.setTarget(instance);
            config.setTargetClass(clazz);
            if(config.pointCutMatch()){
                instance = createProxy(config).getProxy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private AopProxy createProxy(AdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        //如果接口数量 > 0则使用JDK原生动态代理
        if(targetClass.getInterfaces().length > 0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy();
    }

    private AdvisedSupport getAopConfig() {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);

    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Class<?> wrappedClass = beanWrapper.getWrappedClass();
        //获得所有的成员变量
        Field[] fields = wrappedClass.getDeclaredFields();

        for (Field field : fields) {
            //如果没有被Autowired注解的成员变量则直接跳过
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            try {
                field.setAccessible(true);
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){
                    continue;
                }
                field.set(beanWrapper.getWrappedInstance(), this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private Object getSingleton(String beanName) {

        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }
}
