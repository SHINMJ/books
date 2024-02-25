package com.example.books.ui;

import com.example.books.exception.dto.ErrorResponse;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.BookUsecase;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.member.MemberUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "사용자 정보 API")
@RequiredArgsConstructor
@RestController
public class MemberController {
    private static final Integer DEFAULT_SIZE = 20;

    private final BookUsecase book;
    private final MemberUsecase member;

    @Operation(summary = "사용자의 위탁 도서 목록 조회", description = "로그인 정보를 통해 사용자의 위탁 도서 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Invalid Token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
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

    @Operation(summary = "사용자의 대여 도서 목록 조회", description = "로그인 정보를 통해 사용자가 대여한 도서 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Invalid Token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/members/books/borrowed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BookResponse>> myBorrowedBookList(@AuthenticationPrincipal LoginUser loginUser){
        List<BookResponse> responses = member.myBorrowedBookList(loginUser);
        return ResponseEntity.ok(responses);
    }

}
