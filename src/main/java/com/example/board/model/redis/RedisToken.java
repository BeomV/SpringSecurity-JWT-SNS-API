package com.example.board.model.redis;

import com.example.board.model.entity.RedisEntity;

public record RedisToken(
        Long userId,
        String refreshToken
) {

    public static RedisToken from(RedisEntity redisEntity){
        return new RedisToken(
                redisEntity.getUserId(),
                redisEntity.getRefreshToken()
        );
    }

}
