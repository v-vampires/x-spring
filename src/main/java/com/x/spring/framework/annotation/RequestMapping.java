package com.x.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author yifanl
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
