package com.example.books.usecase.book.dto;

import com.example.books.domain.book.Book;
import com.example.books.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Schema(description = "도서 위탁 요인DTO")
public record ConsignmentRequest(@NotBlank String title, @NotBlank String isbn, @NotBlank @PositiveOrZero BigDecimal price) {
    public Book toEntity(Member owner) {
        return Book.create(title, isbn, price, owner);
    }
}
