package com.example.books.exception;

public class BizException extends RuntimeException{
    public BizException() {
        super("관리자에 문의하세요.");
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
