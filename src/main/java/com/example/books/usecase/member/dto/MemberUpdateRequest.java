package com.example.books.usecase.member.dto;

import com.example.books.domain.member.Member;

public record MemberUpdateRequest(String password, String name, String phone) {

    public Member toEntity() {
        return Member.of(name, phone);
    }
}
