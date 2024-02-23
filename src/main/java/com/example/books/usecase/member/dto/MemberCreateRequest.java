package com.example.books.usecase.member.dto;

import com.example.books.domain.member.Member;
import com.example.books.ui.validate.Password;
import com.example.books.ui.validate.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest(@Email String email,
                                  @Password String password,
                                  @NotBlank(message = "이름은 필수입니다.") String name,
                                  @PhoneNumber String phone) {

    public Member toEntity(){
        return Member.of(email, password, name, phone);
    }
}
