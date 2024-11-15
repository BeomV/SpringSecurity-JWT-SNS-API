package com.example.board.controller;


import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/user/login")
    public String userLogin(){

        return "/user/login";
    }

    @GetMapping("/user/mypage")
    public String getMypage(){

        return "/user/mypage";
    }

}
