package com.x.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author yifanl
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";
}
