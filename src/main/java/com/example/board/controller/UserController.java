package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.*;
import com.example.board.service.JwtService;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtService jwtService;
    private final PostService postService;

    public UserController(UserService userService, JwtService jwtService, PostService postService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody userSignUpRequestBody){
        var user = userService.signUp(
                userSignUpRequestBody.username(),
                userSignUpRequestBody.password()
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody UserLoginRequestBody, HttpServletResponse response){
        var userResponse = userService.authenticate(
                UserLoginRequestBody.username(),
                UserLoginRequestBody.password(),
                response
        );
        return ResponseEntity.ok(userResponse);
    }



    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query){
        var users = userService.getUsers(query);
        log.info("/api/v1/users, GetMapping Ok..");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username){
        var user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody UserPatchRequestBody userPatchRequestBody, Authentication authentication){
        var user = userService.updateUser(username,userPatchRequestBody,(UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostsByUserName(@PathVariable String username){
        var posts = postService.getPostByUsername(username);

        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{username}/follows")
    public ResponseEntity<User> follow(@PathVariable String username, Authentication authentication){
        var user = userService.follow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);

    }

    @DeleteMapping("/{username}/follows")
    public ResponseEntity<User> unfollow(@PathVariable String username, Authentication authentication){
        var user = userService.unfollow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);

    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<User>> getFollowersByUser(@PathVariable String username){
        var followers = userService.getFollowersByUser(username);
        return ResponseEntity.ok(followers);

    }

    @GetMapping("/{username}/followings")
    public ResponseEntity<List<User>> getFollowingsByUser(@PathVariable String username){
        var followings = userService.getFollowingsByUser(username);
        return ResponseEntity.ok(followings);

    }

}
