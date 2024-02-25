package com.example.books.usecase.book.dto;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class BookResponsePage extends PageImpl<BookResponse> {
    public BookResponsePage(List<BookResponse> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
