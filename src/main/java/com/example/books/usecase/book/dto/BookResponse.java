package com.example.books.usecase.book.dto;

import com.example.books.domain.book.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "도서 정보 응답DTO")
public record BookResponse(Long id, String title, String isbn, BigDecimal price, String owner, boolean isBorrowed) {
    public static BookResponse of(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getIsbn(), book.getPrice(), book.getOwnerName(), book.isBorrowed());
    }
}
