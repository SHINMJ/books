package com.example.books.exception;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException() {
        super("잘못된 토큰 입니다.");
    }

    public TokenValidationException(String s) {
        super(s);
    }
}
