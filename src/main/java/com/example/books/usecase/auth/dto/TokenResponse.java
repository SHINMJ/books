package com.example.books.usecase.auth.dto;

public record TokenResponse (String token){

    public static TokenResponse valueOf(String token){
        return new TokenResponse(token);
    }
}
