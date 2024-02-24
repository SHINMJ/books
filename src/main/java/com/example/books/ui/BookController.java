package com.example.books.ui;

import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.BookUsecase;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.book.dto.BorrowedRequest;
import com.example.books.usecase.book.dto.ConsignmentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookUsecase service;
    private static final Integer DEFAULT_SIZE = 20;

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> consignment(@Valid @RequestBody ConsignmentRequest request, @AuthenticationPrincipal LoginUser loginUser){
        BookResponse response = service.consignment(request, loginUser);
        return ResponseEntity.created(URI.create("/books/"+response.id())).build();
    }

    @GetMapping("/books")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<BookResponse>> getList(@RequestParam("page") Integer page, @RequestParam(value = "size", required = false) Integer size,
                                                      @RequestParam("order") String order, @RequestParam("direction") String direction){

        Page<BookResponse> responses =
                service.findPageAllBy(
                        PageRequest.of(page, size == null ? DEFAULT_SIZE : size, Sort.by(Sort.Direction.valueOf(direction), order))
                );

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/books/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> rent(@RequestBody BorrowedRequest request, @AuthenticationPrincipal LoginUser loginUser){
        service.borrowed(request, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
