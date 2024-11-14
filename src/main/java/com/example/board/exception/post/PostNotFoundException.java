package com.example.board.exception.post;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ClientErrorException {

    public PostNotFoundException(){
        super(HttpStatus.NOT_FOUND, "Post Not Found");
    }

    public PostNotFoundException(Long postId){
        super(HttpStatus.NOT_FOUND, "Post Not replyId: "+ postId +" Found");
    }

    public PostNotFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }
}
