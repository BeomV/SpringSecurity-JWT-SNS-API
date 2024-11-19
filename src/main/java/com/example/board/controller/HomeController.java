package com.example.board.controller;


import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login")
    public String userLogin(){

        return "/user/login";
    }

    @GetMapping("/user/mypage")
    public String getMypage() {
        return "/user/mypage";
    }

    @GetMapping("/api/user/mypage")
    public ResponseEntity<User> getMypage2(Authentication authentication) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal(); // 현재 인증된 사용자 정보
        User user = userService.getUser(userEntity.getUsername());
        return ResponseEntity.ok(user); // 사용자 데이터 반환
    }

    @GetMapping("/board/board")
    public String getPosts(){
        return "/board/board";
    }



}
