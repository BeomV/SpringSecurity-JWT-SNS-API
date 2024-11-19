package com.example.board.service;

import com.example.board.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    // Access Token 생성
    public String generateAccessToken(UserDetails userDetails){
        return generateAccessToken(userDetails.getUsername(), 1000 * 10 * 1); // 3 hours
    }

    // Generate refresh token with a longer expiration time (e.g., 7 days)
    public String generateRefreshToken(UserDetails userDetails){
        return generateRefreshToken(userDetails.getUsername(), 1000 * 60 * 60 * 24 * 7); // 7 days
    }

    public String getUsername(String accessToken){
        return getSubject(accessToken);
    }

    private String generateAccessToken(String subject, long expirationMillis){
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .signWith(key)
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    private String generateRefreshToken(String subject, long expirationMillis){
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .signWith(key)
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    private String getSubject(String token){

        try{
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }
        catch (JwtException e){
            logger.error("JwtException", e);
            throw e;
        }
    }



}
