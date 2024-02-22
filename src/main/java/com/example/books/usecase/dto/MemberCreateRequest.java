package com.example.books.usecase.dto;

import com.example.books.domain.member.Member;

public record MemberCreateRequest(String email, String encodedPassword, String name, String phone) {

    public Member toEntity(){
        return Member.of(email, encodedPassword, name, phone);
    }
}
