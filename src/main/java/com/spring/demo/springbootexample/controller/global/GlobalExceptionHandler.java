package com.spring.demo.springbootexample.controller.global;


import com.spring.demo.springbootexample.beans.msg.Response;
import com.spring.demo.springbootexample.constants.ErrCodeEnum;
import com.spring.demo.springbootexample.exception.JWTExpiredException;
import com.spring.demo.springbootexample.exception.JWTIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Response defaultExceptionHandler(HttpServletResponse response, Exception exception){
        response.setStatus(500);
        log.error("exception 异常");
        Response res = new Response();
        return res.msg(ErrCodeEnum.SYS_ERROR.code, ErrCodeEnum.SYS_ERROR.msg);
    }

    @ExceptionHandler(JWTIllegalException.class)
    public Response JWTIllegalExceptionHandler(HttpServletResponse response, Exception exception){
        response.setStatus(500);
        log.error("demo 非法 异常");
        Response res = new Response();
        return res.msg(ErrCodeEnum.JWT_ILL_ERROR.code, ErrCodeEnum.JWT_ILL_ERROR.msg);
    }
    @ExceptionHandler(JWTExpiredException.class)
    public Response JWTExpiredExceptionHandler(HttpServletResponse response, Exception exception){
        response.setStatus(500);
        log.error("demo 超时 异常");
        Response res = new Response();
        return res.msg(ErrCodeEnum.JWT_AUTH_FAIL.code, ErrCodeEnum.JWT_AUTH_FAIL.msg);
    }
}
