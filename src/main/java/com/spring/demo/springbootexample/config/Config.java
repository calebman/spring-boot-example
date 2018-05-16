package com.spring.demo.springbootexample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:application.yml"}, encoding = "utf-8")
public class Config {

    @Value("${resources.midpHost}")
    private String midpHost;

    @Value("${jwt.SECRET}")
    private String secret;

    @Value("${jwt.EXPIRATIONTIME}")
    private long expirationtime;

    public String getSecret() {
        return secret;
    }

    public long getExpirationtime() {
        return expirationtime;
    }

    public String getMidpHost() {
        return midpHost;
    }
}
