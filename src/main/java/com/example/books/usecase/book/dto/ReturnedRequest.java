package com.example.books.usecase.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "도서 반납 요청DTO")
public record ReturnedRequest (List<Long> ids) {
}
