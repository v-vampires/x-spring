package com.x.spring.framework.aop.config;

import lombok.Data;

/**
 * @author yifanl
 * @Date 2020/8/1 17:45
 */
@Data
public class AopConfig {

    //切点表达式
    private String pointCut;

    //前置通知方法
    private String aspectBefore;

    //后置通知方法
    private String aspectAfter;

    //切面类
    private String aspectClass;

    //异常通知方法
    private String aspectAfterThrow;

    //抛出的异常类型
    private String aspectAfterThrowingName;

}
