package com.spring.demo.springbootexample.exception;

/**
 * JWT过期异常
 */
public class JWTExpiredException extends Exception{
    public JWTExpiredException(String msg){
        super(msg);
    }
}
