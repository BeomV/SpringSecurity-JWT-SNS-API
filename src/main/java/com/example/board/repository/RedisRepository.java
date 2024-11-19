package com.example.board.repository;

import com.example.board.model.entity.RedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisRepository extends CrudRepository<RedisEntity, Long> {
    Optional<RedisEntity> findByUserId(Long userId);

    Optional<RedisEntity> findByRefreshToken(String refreshToken);

}
