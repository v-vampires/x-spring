package com.x.spring.framework.webmvc.servlet;

import com.alibaba.fastjson.JSON;
import com.x.spring.framework.annotation.RequestParam;
import com.x.spring.framework.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yifanl
 * @Date 2020/8/2 16:01
 */
public class HandlerAdapter {
    String handle(HttpServletRequest request, HttpServletResponse response, HandlerMapping handlerMapping) throws Exception{
        //把方法的形参列表和request的参数列表所在顺序进行一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<>();
        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof RequestParam) {
                    String paramName = ((RequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        //提取方法中的request和response参数
        Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> type = paramsTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }
        //获得方法的形参列表
        Map<String, String[]> params = request.getParameterMap();
        //controller的方法实参列表
        Object[] paramValues = new Object[paramsTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = parseStringValue(value, paramsTypes[index]);
        }
        //填充HttpServletRequest参数
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }
        //填充HttpServletResponse参数
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }
        //反射调用controller的方法
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }
        //解析controller的方法返回
        Class<?> returnType = handlerMapping.getMethod().getReturnType();
        if(returnType == Void.class){
            return null;
        }else if(returnType == String.class){
            return (String) result;
        }else{
            if(handlerMapping.getMethod().isAnnotationPresent(ResponseBody.class)){
                // to json
                return JSON.toJSONString(result);
            }else{
                throw new Exception("返回类型不正确");
            }
        }

    }

    /**
     * request中接收的参数都是string类型的，需要转换为controller中实际的参数类型
     * 暂时只支持string、int、double类型
     */
    private Object parseStringValue(String value, Class<?> paramsType) {
        if (String.class == paramsType) {
            return value;
        }
        if (Integer.class == paramsType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }
        //还有，继续加if
    }
}
