package com.sky.service;

import com.sky.enumeration.JwtType;
import io.jsonwebtoken.Claims;

import java.util.Map;

public interface JwtService {
    String generate(JwtType jwtType, Map<String, Object> claims);

    Claims parse(JwtType jwtType, String token);
}
