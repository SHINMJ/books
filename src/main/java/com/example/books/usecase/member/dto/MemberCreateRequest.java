package com.example.books.usecase.member.dto;

import com.example.books.domain.member.Member;
import com.example.books.ui.validate.Password;
import com.example.books.ui.validate.PhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 가입 요청 DTO")
public record MemberCreateRequest(@NotBlank @Email String email,
                                  @NotBlank @Password String password,
                                  @NotBlank(message = "이름은 필수입니다.") String name,
                                  @NotBlank @PhoneNumber String phone) {

    public Member toEntity(){
        return Member.of(email, password, name, phone);
    }
}
