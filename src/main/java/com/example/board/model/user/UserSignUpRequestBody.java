package com.example.board.model.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//Dto 방식 Getter/Setter 필요 없음
public record UserSignUpRequestBody(

        @NotEmpty
        String username,

        @NotEmpty
        String password
) {

}

