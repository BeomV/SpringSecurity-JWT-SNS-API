package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotAllowedException extends ClientErrorException {

    public UserNotAllowedException(){
        super(HttpStatus.FORBIDDEN, "User Not Allowed");
    }

}
