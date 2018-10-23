package com.spring.demo.springbootexample.config;

import com.spring.demo.springbootexample.intercepter.JWTAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiger implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getJWTAuthInterceptor()).excludePathPatterns("/error","/user/login");
    }

    @Bean
    public HandlerInterceptor getJWTAuthInterceptor(){
        return new JWTAuthInterceptor();
    }
}
