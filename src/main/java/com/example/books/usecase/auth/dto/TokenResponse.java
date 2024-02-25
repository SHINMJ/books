package com.example.books.usecase.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답DTO")
public record TokenResponse (String token){

    public static TokenResponse valueOf(String token){
        return new TokenResponse(token);
    }
}
