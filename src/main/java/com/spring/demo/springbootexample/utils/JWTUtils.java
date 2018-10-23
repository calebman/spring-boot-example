package com.spring.demo.springbootexample.utils;


import com.spring.demo.springbootexample.beans.entity.JWTUser;
import com.spring.demo.springbootexample.constants.JWTConsts;
import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {

    public static String createJWT(JWTUser user, String jwtId, String subject, String secret, long ttlMillis){
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWTConsts.NAME, user.getUserName());
        claims.put(JWTConsts.PASSWORD, user.getPassword());
        return createJWT(claims, jwtId, subject, secret, ttlMillis);
    }

    public static String createJWT(Map<String, Object> claims, String jwtId, String subject,String secret, long ttlMillis){
        // 指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        // 生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        SecretKey key = getKey(secret);
        // 下面就是在为payload添加各种标准声明和私有声明了
        // 这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(jwtId)
                // iat: jwt的签发时间
                .setIssuedAt(now)
                // sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            // 设置过期时间
            builder.setExpiration(exp);
        }
        // 就开始压缩为xxxxxxxxxxxxxx.xxxxxxxxxxxxxxx.xxxxxxxxxxxxx这样的jwt
        return builder.compact();
    }
    public static Jws<Claims> parseJWT(String jwt, String secret) throws Exception {
        // 签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = getKey(secret);
        // 得到DefaultJwtParser
        Jws<Claims> jwsClaims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(key)
                //设置需要解析的jwt
                .parseClaimsJws(jwt);
        return jwsClaims;
    }

    public static JWTUser parseJWTInfo(String jwt, String secret) throws Exception {

        Claims claims = parseJWT(jwt,secret).getBody();
        JWTUser jwtUser = JWTUser.builder().userName(claims.get(JWTConsts.NAME,String.class)).build();
        return jwtUser;
    }

    public static SecretKey getKey(String secret){

        byte[] encodedKey = Base64.decodeBase64(secret);
        // 根据给定的字节数组使用AES加密算法构造一个密钥，使用 encodedKey中的始于且包含 0 到前 leng 个字节这是当然是所有。（后面的文章中马上回推出讲解Java加密和解密的一些算法）
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
