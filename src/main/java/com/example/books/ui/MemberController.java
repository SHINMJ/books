package com.example.books.ui;

import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.BookUsecase;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.member.MemberUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private static final Integer DEFAULT_SIZE = 20;

    private final BookUsecase book;
    private final MemberUsecase member;

    @GetMapping("/members/books")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookResponse>> myBookList(@RequestParam("page") Integer page,
                                                         @RequestParam(value = "size", required = false) Integer size,
                                                         @RequestParam(value = "order", required = false, defaultValue = "numberOfBorrowed") String order,
                                                         @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
                                                         @AuthenticationPrincipal LoginUser loginUser){
        Page<BookResponse> responses =
                book.myBookList(
                        PageRequest.of(page, size == null ? DEFAULT_SIZE : size,
                                Sort.by(Sort.Direction.valueOf(direction), order)),
                        loginUser
                );
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/members/books/borrowed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BookResponse>> myBorrowedBookList(@AuthenticationPrincipal LoginUser loginUser){
        List<BookResponse> responses = member.myBorrowedBookList(loginUser);
        return ResponseEntity.ok(responses);
    }

}
