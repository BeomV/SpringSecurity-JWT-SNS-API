package com.example.board.exception.jwt;

import io.jsonwebtoken.JwtException;

public class JwtTokenNotFoundException extends JwtException {

    public JwtTokenNotFoundException() {
        super("Jwt Token Not Found");
    }
}
