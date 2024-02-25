package com.example.books.usecase.book;

import com.example.books.domain.book.Book;
import com.example.books.domain.book.BookRepository;
import com.example.books.domain.member.Member;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.book.dto.BorrowedRequest;
import com.example.books.usecase.book.dto.ConsignmentRequest;
import com.example.books.usecase.book.dto.ReturnedRequest;
import com.example.books.usecase.member.MemberUsecase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookUsecaseTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberUsecase memberUsecase;

    @InjectMocks
    @Spy
    private BookUsecase bookUsecase;

    private LoginUser loginUser = mock(LoginUser.class);

    Member owner = Member.of("owner@email.com", "1111", "owner", "010-9999-2223");
    Member borrower = Member.of("borrower@email.com", "1111", "borrower", "010-9999-2453");


    @Test
    void consignment_success() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        when(memberUsecase.findById(anyLong()))
                .thenReturn(owner);
        when(bookRepository.save(any()))
                .thenReturn(book);

        ConsignmentRequest request = new ConsignmentRequest(book.getTitle(), book.getIsbn(), book.getPrice());
        BookResponse response = bookUsecase.consignment(request, loginUser);

        assertEquals(response.owner(), book.getOwnerName());
    }

    @Test
    void findAllBy() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "numberOfBorrowed"));

        when(bookRepository.findAllBy(pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));

        Page<BookResponse> list = bookUsecase.findPageAllBy(pageable);

        assertEquals(list.getTotalElements(), 1);
    }

    @Test
    void borrowed() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        BorrowedRequest request = new BorrowedRequest(List.of(1L));

        when(bookRepository.findAllById(any()))
                .thenReturn(List.of(book));

        bookUsecase.borrowed(request, loginUser);

        assertTrue(book.isBorrowed());

        await()
                .atMost(Duration.ofSeconds(11L))
                .untilAsserted(() -> {
                    assertFalse(book.isBorrowed());
                });
    }

    @Test
    void myBookList() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "numberOfBorrowed"));

        when(memberUsecase.findById(anyLong()))
                .thenReturn(owner);
        when(bookRepository.findAllByOwner(any(), any()))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));

        Page<BookResponse> responses = bookUsecase.myBookList(pageable, loginUser);

        assertEquals(responses.getTotalElements(), 1);
    }
}