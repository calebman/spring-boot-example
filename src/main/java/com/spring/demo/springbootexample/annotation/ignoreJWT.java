package com.spring.demo.springbootexample.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在使用该注解的方法上不进行jwt校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface ignoreJWT {
}
