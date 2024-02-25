package com.example.books.usecase.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청DTO")
public record LoginRequest(@NotBlank String email, @NotBlank String password) {
}
