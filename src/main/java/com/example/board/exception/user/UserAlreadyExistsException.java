package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ClientErrorException {

    public UserAlreadyExistsException(){
        super(HttpStatus.CONFLICT, "User Already Exists");
    }

    public UserAlreadyExistsException(String username){
        super(HttpStatus.CONFLICT, "User Not userId: "+ username +" Already Exists.");
    }

}
