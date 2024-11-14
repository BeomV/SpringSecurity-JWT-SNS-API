package com.example.board.model.user;

import jakarta.validation.constraints.NotEmpty;

//Dto 방식 Getter/Setter 필요 없음
public record UserLoginRequestBody(

        @NotEmpty
        String username,

        @NotEmpty
        String password) {

}

