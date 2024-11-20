package com.example.board.exception.jwt;

import io.jsonwebtoken.JwtException;

public class JwtTokenExpiredException extends JwtException {

    public JwtTokenExpiredException() {
        super("AccessToken Already Expired");
    }
}
