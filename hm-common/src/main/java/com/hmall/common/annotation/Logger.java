package com.hmall.common.annotation;

import java.lang.annotation.*;

/**
 * 自定义日志注解
 * @author Fu1sh
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logger {

    String description() default "";
}
