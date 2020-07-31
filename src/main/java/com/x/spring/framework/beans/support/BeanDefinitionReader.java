package com.x.spring.framework.beans.support;

import com.x.spring.framework.annotation.Component;
import com.x.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author yifanl
 * @Date 2020/7/31 22:04
 * 读取配置文件，扫描相关的类，解析成BeanDefinition
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    //配置文件中指定需要扫描的包名
    private final String SCAN_PACKAGE = "scan.package";

    private List<String> registyBeanClasses = new ArrayList<>();

    public BeanDefinitionReader(String... locations) {
        //1.定位，通过URL定位找到配置文件，然后转换为文件流
        //2 加载，保存为properties
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3. 扫描，扫描资源文件(class)，并保存到集合中
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replace("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){
                    continue;
                }
                Annotation[] annotations = beanClass.getAnnotations();
                if(annotations.length == 0){
                    continue;
                }
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    //只考虑被@Component注解的class
                    if(annotationType.isAnnotationPresent(Component.class)){
                        //beanName有三种情况：
                        //1. 默认是类名首字母小写
                        //2. 自定义名字（这里暂不考虑）
                        //3. 接口注入
                        result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                        Class<?>[] interfaces = beanClass.getInterfaces();
                        for (Class<?> i : interfaces) {
                            //接口和实现类之间的关系也需要封装
                            result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
