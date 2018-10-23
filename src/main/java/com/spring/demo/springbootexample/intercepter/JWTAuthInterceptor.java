package com.spring.demo.springbootexample.intercepter;


import com.spring.demo.springbootexample.annotation.ignoreJWT;
import com.spring.demo.springbootexample.beans.entity.JWTUser;
import com.spring.demo.springbootexample.constants.JWTConsts;
import com.spring.demo.springbootexample.exception.JWTIllegalException;
import com.spring.demo.springbootexample.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JWTAuthInterceptor extends HandlerInterceptorAdapter {
    @Value("${jwt.key}")
    private String JWTKey;

    public JWTAuthInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("开始校验JWT");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ignoreJWT ignoreAnnotation = handlerMethod.getBeanType().getAnnotation(ignoreJWT.class);
        if (ignoreAnnotation == null){
            ignoreAnnotation = handlerMethod.getMethodAnnotation(ignoreJWT.class);
        }
        if (ignoreAnnotation != null){
            log.info("当前方法不进行jwt校验");
            return super.preHandle(request, response, handler);
        }
        String jwtToken = request.getHeader(JWTConsts.JWT_HEADER_KEY);
        if (!StringUtils.hasText(jwtToken)){
            log.error("no JWT Token");
            throw new JWTIllegalException("no jwt token");
        }
        try{
            JWTUser userInfo = JWTUtils.parseJWTInfo(jwtToken, JWTKey);
            log.info("用户信息:{}", userInfo);
            request.setAttribute("userInfo", userInfo);
        }catch (ExpiredJwtException e){
            log.error("JWT Token 失效");
        }catch (Exception e){
            throw new RuntimeException("JWT 异常");
        }
        return super.preHandle(request, response, handler);
    }

}
