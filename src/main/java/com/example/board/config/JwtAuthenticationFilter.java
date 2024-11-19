package com.example.board.config;

import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
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
        // TODO: JWT 검증
        String BEARER_PREFIX = "Bearer ";
        var authoriztion = request.getHeader(HttpHeaders.AUTHORIZATION);
        var securityContext = SecurityContextHolder.getContext();

        /*if(ObjectUtils.isEmpty(authoriztion) || !authoriztion.startsWith(BEARER_PREFIX)){
            throw new JwtTokenNotFoundException();
        }*/

        if(!ObjectUtils.isEmpty(authoriztion) && authoriztion.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null){

            var accessToken = authoriztion.substring(BEARER_PREFIX.length());

            var username = jwtService.getUsername(accessToken);
            var userDetails = userService.loadUserByUsername(username);

            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null,userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);

        }


        filterChain.doFilter(request, response);
    }
}
