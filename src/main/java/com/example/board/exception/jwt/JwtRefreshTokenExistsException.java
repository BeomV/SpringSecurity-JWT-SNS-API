package com.example.board.exception.jwt;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class JwtRefreshTokenExistsException extends ClientErrorException {

    public JwtRefreshTokenExistsException(){
        super(HttpStatus.CONFLICT, "refreshToken ID Already Exists");
    }


}
