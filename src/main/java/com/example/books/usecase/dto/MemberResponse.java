package com.example.books.usecase.dto;

import com.example.books.domain.member.Member;

public record MemberResponse(Long id, String email, String name, String phone) {

    public static MemberResponse of(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getName(), member.getPhone());
    }
}
