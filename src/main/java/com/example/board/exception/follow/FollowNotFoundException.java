package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends ClientErrorException {

    public FollowNotFoundException(){
        super(HttpStatus.NOT_FOUND, "Follow Not Found");
    }

    public FollowNotFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }
}
