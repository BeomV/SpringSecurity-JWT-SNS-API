package com.example.board.exception.jwt;

import io.jsonwebtoken.JwtException;

public class JwtTokenSigatureNotMatchException extends JwtException {

    public JwtTokenSigatureNotMatchException() {
        super("Jwt Token Signature Not Match");
    }
}
