package com.example.board.service;

import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.exception.jwt.JwtTokenSigatureNotMatchException;
import com.example.board.exception.user.UserNotLoggedInException;
import com.example.board.model.user.UserRefershAccessTokenResponse;
import com.example.board.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret-key}")
    private String SECRET;

    private SecretKey key;

    private final UserService userService;

    //@Lazy 순환 참조 (지연로드)
    public JwtService(@Lazy UserService userService) {
        this.userService = userService;
    }
    @PostConstruct
    public void init() {
        if (SECRET == null || SECRET.length() < 32) {
            throw new IllegalArgumentException();
        }
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes()); // 문자열을 SecretKey로 변환
    }

    // Access Token 생성
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(userDetails.getUsername(), 1000 * 30 * 1); // 3 hours
    }

    // Generate refresh token with a longer expiration time (e.g., 7 days)
    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(userDetails.getUsername(), 1000 * 60 * 60 * 24 * 7); // 7 days
//        return generateRefreshToken(userDetails.getUsername(), 1000 * 10 * 1); // 7 days
    }

    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    private String generateAccessToken(String subject, long expirationMillis) {
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .signWith(key, SignatureAlgorithm.HS256)
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    private String generateRefreshToken(String subject, long expirationMillis) {
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .signWith(key, SignatureAlgorithm.HS256)
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    private String getSubject(String token) {

        try {
            var returnResult = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            return returnResult;
        } catch (SignatureException e) {
            throw new JwtTokenSigatureNotMatchException();
        }
    }


    public UserRefershAccessTokenResponse generateRefreshAccessToken(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies == null) {
            throw new JwtTokenNotFoundException();
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        if (refreshToken == null || refreshToken.isBlank() || refreshToken.isEmpty()) {
            throw new JwtTokenNotFoundException();
        }
            var username = getUsername(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(username);
            var newAccessToken = generateAccessToken(userDetails);
            return new UserRefershAccessTokenResponse(newAccessToken);

    }

}




