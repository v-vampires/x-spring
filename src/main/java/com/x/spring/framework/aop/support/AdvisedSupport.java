package com.x.spring.framework.aop.support;

import com.x.spring.framework.aop.aspect.AfterReturningAdviceInterceptor;
import com.x.spring.framework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.x.spring.framework.aop.aspect.MethodBeforeAdviceInterceptor;
import com.x.spring.framework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yifanl
 * @Date 2020/8/1 17:37
 * 封装了创建代理所需要的一切资源，封装了被代理的目标实例、拦截器链等，还负责解析AOP配置和创建拦截器
 */
public class AdvisedSupport {
    /**被代理的类class*/
    private Class<?> targetClass;
    /**被代理的对象实例*/
    private Object target;
    /**被代理的方法对应的拦截器集合*/
    private Map<Method, List<Object>> methodCache;
    /**AOP外部配置*/
    private AopConfig config;

    /**切点正则表达式*/
    private Pattern pointCutClassPattern;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 获取拦截器
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }
    /**
     * 解析AOP配置，创建拦截器
     */
    private void parse(){
        //编译切点表达式为正则
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //pointCut=public .* com.x.demo.service..*Service..*(.*)
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {
            //保存切面的所有通知方法
            Map<String, Method> aspectMethods = new HashMap<>();
            Class aspectClass = Class.forName(this.config.getAspectClass());
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }
            methodCache = new HashMap<>();
            Pattern pattern = Pattern.compile(pointCut);
            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                //为了能正确匹配这里去除函数签名尾部的throws xxxException
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<>();

                    //创建前置拦截器
                    if (config.getAspectBefore() != null && !config.getAspectBefore().equals("")) {
                        //创建一个Advivce
                        MethodBeforeAdviceInterceptor beforeAdvice = new MethodBeforeAdviceInterceptor(
                                aspectMethods.get(config.getAspectBefore()),
                                aspectClass.newInstance()
                        );
                        advices.add(beforeAdvice);
                    }
                    //创建后置拦截器
                    if (config.getAspectAfter()!= null && !"".equals(config.getAspectAfter())) {
                        AfterReturningAdviceInterceptor returningAdvice = new AfterReturningAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfter()),
                                aspectClass.newInstance()
                        );
                        advices.add(returningAdvice);
                    }

                    if (config.getAspectAfterThrow() != null && !"".equals(config.getAspectAfterThrow())) {
                        AfterThrowingAdviceInterceptor throwingAdvice = new AfterThrowingAdviceInterceptor(
                                aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance()
                        );
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m, advices);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 判断一个类是否需要被代理
     */
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
