package com.example.books.exception;

public class SamePasswordException extends BizException {
    public SamePasswordException() {
        super("이전과 같은 비밀번호 입니다. 다른 비밀번호를 사용하세요.");
    }
}
