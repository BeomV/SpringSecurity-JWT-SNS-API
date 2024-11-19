package com.example.board.service;

import com.example.board.exception.jwt.JwtRefreshTokenExistsException;
import com.example.board.model.entity.RedisEntity;
import com.example.board.repository.RedisRepository;
import com.example.board.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RedisService {

    private final RedisRepository repository;
    private final RedisRepository redisRepository;

    public RedisService(RedisRepository repository, RedisRepository redisRepository) {
        this.repository = repository;
        this.redisRepository = redisRepository;
    }



    public void saveRefreshToken(Long userId, String refreshToken){
        var redisEntity = repository.save(new RedisEntity(userId, refreshToken));


    }




}
