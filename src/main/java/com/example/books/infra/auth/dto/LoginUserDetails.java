package com.example.books.infra.auth.dto;

import com.example.books.domain.member.Member;
import com.example.books.infra.auth.userdetails.CustomUserDetails;
import com.example.books.usecase.auth.dto.LoginUser;

public class LoginUserDetails extends CustomUserDetails implements LoginUser {
    private final Member member;

    public LoginUserDetails(Member member) {
        super(member);
        this.member = member;
    }

    @Override
    public Long getId() {
        return member.getId();
    }

    @Override
    public String getEmail() {
        return member.getEmail();
    }
}
