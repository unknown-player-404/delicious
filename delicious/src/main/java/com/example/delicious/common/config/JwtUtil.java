package com.example.delicious.common.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    /**
     * 创建Token
     */
    public String generateToken(Map<String, Object> claims){
        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .claims(claims)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
