package com.example.board.service;

import com.example.board.exception.follow.FollowAlreadyExistsException;
import com.example.board.exception.follow.FollowNotFoundException;
import com.example.board.exception.follow.InvalidFollowException;
import com.example.board.exception.jwt.JwtRefreshTokenExistsException;
import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.FollowEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.repository.FollowRepository;
import com.example.board.repository.RedisRepository;
import com.example.board.repository.UserRepository;
import com.example.board.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FollowRepository followRepository;
    private final RedisService redisService;
    private final RedisRepository redisRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, FollowRepository followRepository, RedisService redisService, RedisRepository redisRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.followRepository = followRepository;
        this.redisService = redisService;
        this.redisRepository = redisRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        return user;
    }

    public User signUp(String username, String password) {

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException();
                });

        var userEntity = UserEntity.of(username,bCryptPasswordEncoder.encode(password));
        var savedUserEntity = userRepository.save(userEntity);

        return User.from(savedUserEntity);


    }

    @Transactional
    public UserAuthenticationResponse authenticate(String username, String password, HttpServletResponse response) {

        var userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        if(passwordEncoder.matches(password, userEntity.getPassword())){
            var accessToken = jwtService.generateAccessToken(userEntity);
            var refreshToken = jwtService.generateRefreshToken(userEntity);

            redisService.saveRefreshToken(userEntity.getUserId(),refreshToken);

            response.addCookie(CookieUtil.createHttpOnlyCookie("refreshToken", refreshToken));
            return new UserAuthenticationResponse(accessToken);
        }
        else {
            throw new UserNotFoundException();
        }

    }

    @Transactional
    public UserAuthenticationResponse refreshAccessToken(String refreshToken, HttpServletResponse response) {
        // Redis에서 RefreshToken 확인
        var redisEntity = redisRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtRefreshTokenExistsException());

        // RefreshToken 유효성 검증
        jwtService.validateRefreshToken(refreshToken);

        // AccessToken 재발급
        var user = userRepository.findById(redisEntity.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for RefreshToken."));

        var newAccessToken = jwtService.generateAccessToken(user);

        return new UserAuthenticationResponse(newAccessToken);
    }


    public List<User> getUsers(String query) {
        List<UserEntity> userEntities;

        if(query != null && !query.isBlank()) {
            // TODO: query 검색어 기반, 해당 검색어가 username에 포함 되어 있는 유저목록 가져오기
            userEntities = userRepository.findByUsernameContaining(query);

        }else {
            userEntities = userRepository.findAll();
        }

        return userEntities.stream().map(User::from).toList();
    }

    public User getUser(String username) {
        var userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );
        return User.from(userEntity);
    }

    public User updateUser(String username, UserPatchRequestBody userPatchRequestBody, UserEntity currentUser) {
        var userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        if(!userEntity.equals(currentUser)){
            throw new UserNotAllowedException();
        }

        if(userPatchRequestBody.description() != null){
            userEntity.setDescription(userPatchRequestBody.description());
        }

        return User.from(userRepository.save(userEntity));

    }

    @Transactional
    public User follow(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        if(following.equals(currentUser)){
            throw new InvalidFollowException("A User Cannot Follow Themselves.");
        }

        followRepository.findByFollowerAndFollowing(currentUser,following).ifPresent(
                follow -> {
                    throw new FollowAlreadyExistsException(currentUser,following);
                }
        );
        followRepository.save(FollowEntity.of(currentUser,following));

        following.setFollowersCount(following.getFollowersCount() + 1);
        currentUser.setFollowingsCount(currentUser.getFollowingsCount() +1);

        userRepository.saveAll(List.of(following,currentUser));

        return User.from(following);
    }

    @Transactional
    public User unfollow(String username, UserEntity currentUser) {
        var following = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        if(following.equals(currentUser)){
            throw new InvalidFollowException("A User Cannot unFollow Themselves.");
        }

        var followEntity =
                followRepository.findByFollowerAndFollowing(currentUser,following).orElseThrow(
                        FollowNotFoundException::new);

        followRepository.delete(followEntity);

        following.setFollowersCount(Math.max(0,following.getFollowersCount() - 1));
        currentUser.setFollowingsCount(Math.max(0, currentUser.getFollowingsCount() -1));

        userRepository.saveAll(List.of(following,currentUser));

        return User.from(following);
    }

    public List<User> getFollowersByUser(String username) {
        var following = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        var followEntities = followRepository.findByFollowing(following);
        return followEntities.stream().map(follow -> User.from(follow.getFollower())).toList();
    }

    public List<User> getFollowingsByUser(String username) {
        var follower = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(username)
        );

        var followEntities = followRepository.findByFollower(follower);
        return followEntities.stream().map(follow -> User.from(follow.getFollowing())).toList();
    }
}
