package com.example.board.config;

import com.example.board.exception.jwt.JwtTokenExpiredException;
import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String BEARER_PREFIX = "Bearer ";
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();
        var cookies = request.getCookies();

        try {
            if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null) {
                var accessToken = authorization.substring(BEARER_PREFIX.length());
                setAuthenticationInSecurityContext(accessToken, request);
            }
            else if (!ObjectUtils.isEmpty(authorization) && authorization.startsWith(BEARER_PREFIX) && cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        var refreshToken = cookie.getValue();
                        setAuthenticationInSecurityContext(refreshToken, request);
                        break;
                    }
                }
            }

        }catch (ExpiredJwtException e){
            throw new JwtTokenExpiredException();
        }


        filterChain.doFilter(request, response);
    }

    private void setAuthenticationInSecurityContext(String token, HttpServletRequest request) {
        var username = jwtService.getUsername(token); // 토큰에서 사용자 이름 추출
        var userDetails = userService.loadUserByUsername(username); // 사용자 정보 로드

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        var securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

}
