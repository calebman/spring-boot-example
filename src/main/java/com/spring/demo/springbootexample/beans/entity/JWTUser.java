package com.spring.demo.springbootexample.beans.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class JWTUser {
    private String userName;
    private String password;
}
