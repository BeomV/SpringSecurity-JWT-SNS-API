package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotLoggedInException extends ClientErrorException {

    public UserNotLoggedInException(){
        super(HttpStatus.BAD_REQUEST, "User Not Logged In");
    }


}
