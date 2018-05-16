package com.spring.demo.springbootexample.jwt;


import com.spring.demo.springbootexample.config.Config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

@Component
public class JsonWebTokenUtility {

    @Autowired
    Config config;

    String HEADER_NAME = "Authorization";

    public void addToken(HttpServletResponse res, String userId){


        String JWT = Jwts.builder()
                        .setSubject(userId)
                        .setExpiration(new Date(System.currentTimeMillis() + config.getExpirationtime()))
                        .signWith(SignatureAlgorithm.HS512, config.getSecret())
                        .compact();
        res.addHeader(HEADER_NAME, JWT);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_NAME);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(config.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }

}
