package com.example.board.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static Cookie createHttpOnlyCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        return cookie;
    }
}