package com.sky.service.impl;

import com.sky.enumeration.JwtType;
import com.sky.properties.JwtProperties;
import com.sky.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成Jwt Token
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param claims 设置的信息
     * @return
     */
    public String generate(JwtType jwtType, Map<String, Object> claims) {
        String secretKey;
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long expMillis = System.currentTimeMillis();

        switch (jwtType) {
            case USER:
                secretKey = jwtProperties.getUserSecretKey();
                expMillis += jwtProperties.getUserTtl();
                break;
            case ADMIN:
                secretKey = jwtProperties.getAdminSecretKey();
                expMillis += jwtProperties.getAdminTtl();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + jwtType);
        }
        // 生成JWT的时间
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * 解析Jwt Token
     *
     * @param token     加密后的token
     * @return
     */
    public Claims parse(JwtType jwtType, String token) {
        String secretKey;
        switch (jwtType) {
            case USER:
                secretKey = jwtProperties.getUserSecretKey();
                break;
            case ADMIN:
                secretKey = jwtProperties.getAdminSecretKey();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + jwtType);
        }
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
