package com.example.books.exception;

public class AlreadyExistsEmailException extends RuntimeException {
    public AlreadyExistsEmailException() {
        super("이미 사용 중인 이메일입니다.");
    }
}
