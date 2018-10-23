package com.spring.demo.springbootexample.exception;

/**
 * JWT非法异常
 */
public class JWTIllegalException extends Exception{
    public JWTIllegalException(String msg){
        super(msg);
    }
}
