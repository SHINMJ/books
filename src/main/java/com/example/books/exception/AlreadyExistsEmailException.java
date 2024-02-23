package com.example.books.exception;

public class AlreadyExistsEmailException extends BizException {
    public AlreadyExistsEmailException() {
        super("이미 사용 중인 이메일입니다.");
    }
}
