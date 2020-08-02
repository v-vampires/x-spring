package com.x.spring.framework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author yifanl
 * @Date 2020/8/2 15:49
 * 保存了用户写的Controller实例、所有浏览器能访问到的方法，以及使用@RequestMapping定义的URL表达式
 */
@Data
public class HandlerMapping {
    //保存方法对应的实例
    private Object controller;

    //保存映射的方法
    private Method method;

    //URL的正则匹配
    private Pattern pattern;

    public HandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
