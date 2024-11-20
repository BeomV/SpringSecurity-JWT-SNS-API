package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyLogOutException extends ClientErrorException {

    public UserAlreadyLogOutException(){
        super(HttpStatus.BAD_REQUEST, "User Already Logout");
    }


}
