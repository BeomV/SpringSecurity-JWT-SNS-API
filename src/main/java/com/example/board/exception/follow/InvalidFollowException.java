package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class InvalidFollowException extends ClientErrorException {

    public InvalidFollowException(){
        super(HttpStatus.BAD_REQUEST, "Invalid Follow Request. ");
    }

    public InvalidFollowException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }
}
