package com.spring.demo.springbootexample.controller;

import com.spring.demo.springbootexample.jwt.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class JwtTestController {

    @Autowired
    private JsonWebTokenUtility jwt;

    @RequestMapping(value = "/testJwt")
    @ResponseBody
    public String login(HttpServletResponse response){
        jwt.addToken(response,"ohaha~");

        System.out.println("login controller");
        return "ohaha";
    }

    @RequestMapping(value = "/testJwtdecrypt")
    @ResponseBody
    public String dec(HttpServletRequest request){

        jwt.getAuthentication(request);
        return "ohaha";
    }

}
