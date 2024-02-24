package com.example.books.usecase.book.dto;

import com.example.books.domain.book.Book;

import java.math.BigDecimal;

public record BookResponse(Long id, String title, String isbn, BigDecimal price, String owner, boolean isBorrowed) {
    public static BookResponse of(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getIsbn(), book.getPrice(), book.getOwnerName(), book.isBorrowed());
    }
}
