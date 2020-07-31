package com.x.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author yifanl
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String value() default "";
}
