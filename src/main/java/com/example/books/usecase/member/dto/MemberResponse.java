package com.example.books.usecase.member.dto;

import com.example.books.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 응답DTO")
public record MemberResponse(Long id, String email, String name, String phone) {

    public static MemberResponse of(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getName(), member.getPhone());
    }
}
