package com.spring.demo.springbootexample.controller;

import com.spring.demo.springbootexample.annotation.ignoreJWT;
import com.spring.demo.springbootexample.beans.entity.JWTUser;
import com.spring.demo.springbootexample.beans.msg.Response;
import com.spring.demo.springbootexample.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class TestController {

    @Value("${jwt.key}")
    private String JWTKey;
    @Value("${jwt.expiredTime}")
    private long expiredTime;

    @ignoreJWT
    @PostMapping("/login")
    public Response JWTLogin(String userName, String password){
        log.info("用户登陆:{}",userName);
        Response response = new Response();
        if ("ohaha".equals(userName) && "123456".equals(password)){
            JWTUser user = JWTUser.builder().userName(userName).password(password).build();
            String JWTToken = JWTUtils.createJWT(user, UUID.randomUUID().toString(), user.getUserName(), JWTKey, expiredTime);
            response.setData(JWTToken);
        }else {
            response.fail("登陆失败");
        }
        return response;
    }
    @PostMapping("/test")
    public Response test(){
        log.info("test controller");
        Response response = new Response();
        response.success();
        return response;
    }
}
