package com.example.books.usecase.member.dto;

import com.example.books.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 수정 요청DTO")
public record MemberUpdateRequest(String password, String name, String phone) {

    public Member toEntity() {
        return Member.of(name, phone);
    }
}
