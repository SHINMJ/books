package com.example.books.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("사용자 정보를 찾을 수 없습니다.");
    }

}
