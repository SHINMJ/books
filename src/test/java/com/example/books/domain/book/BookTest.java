package com.example.books.domain.book;

import com.example.books.domain.member.Member;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    Member owner = Member.of("owner@email.com", "1111", "owner", "010-9999-2223");
    Member borrower = Member.of("borrower@email.com", "1111", "borrower", "010-9999-2453");

    @Test
    void isOwner() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        assertTrue(book.isOwner(owner));
        assertFalse(book.isOwner(borrower));
    }

    @Test
    void isBorrowed() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        assertFalse(book.isBorrowed());

        book.borrowed();

        assertTrue(book.isBorrowed());

        book.returned();

        assertFalse(book.isBorrowed());
        assertEquals(book.getNumberOfBorrowed(), 1L);
    }
}