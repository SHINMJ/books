package com.example.books.usecase.book.dto;

import java.util.List;

public record BorrowedRequest(List<Long> ids) {
}
