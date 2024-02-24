package com.example.books.usecase.book.dto;

import com.example.books.domain.book.Book;
import com.example.books.domain.member.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ConsignmentRequest(@NotBlank String title, @NotBlank String isbn, @PositiveOrZero BigDecimal price) {
    public Book toEntity(Member owner) {
        return Book.create(title, isbn, price, owner);
    }
}
