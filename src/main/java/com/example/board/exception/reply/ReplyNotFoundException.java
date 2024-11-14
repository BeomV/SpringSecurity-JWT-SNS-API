package com.example.board.exception.reply;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ClientErrorException {

    public ReplyNotFoundException(){
        super(HttpStatus.NOT_FOUND, "Reply Not Found");
    }

    public ReplyNotFoundException(Long replyId){
        super(HttpStatus.NOT_FOUND, "Reply Not replyId: "+ replyId +" Found");
    }

    public ReplyNotFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }
}
