package com.example.books.ui;

import com.example.books.exception.dto.ErrorResponse;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.book.BookUsecase;
import com.example.books.usecase.book.dto.BookResponse;
import com.example.books.usecase.book.dto.BorrowedRequest;
import com.example.books.usecase.book.dto.ConsignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "도서 위탁 & 대여 API")
@RequiredArgsConstructor
@RestController
public class BookController {
    private static final Integer DEFAULT_SIZE = 20;

    private final BookUsecase service;

    @Operation(summary = "도서 위탁", description = "도서 정보를 통해 도서를 위탁합니다.",
    responses = {
            @ApiResponse(responseCode = "201", description = "도서 위탁 성공", content = @Content(mediaType = "application/json"),
                    headers = @Header(name = "Location", description = "위탁 성공한 도서 위치")),
            @ApiResponse(responseCode = "401", description = "Invalid Token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> consignment(@Valid @RequestBody ConsignmentRequest request, @AuthenticationPrincipal LoginUser loginUser){
        BookResponse response = service.consignment(request, loginUser);
        return ResponseEntity.created(URI.create("/books/"+response.id())).build();
    }

    @Operation(summary = "도서 목록 조회", description = "위탁 된 모든 도서 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "도서 목록 조회 성공",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)))),
                    @ApiResponse(responseCode = "401", description = "Invalid Token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            },
            parameters = {
                @Parameter(name = "page", required = true, description = "조회할 page. 0부터 시작"),
                @Parameter(name = "size", description = "현재 page size", schema = @Schema(defaultValue = "20")),
                @Parameter(name = "order", required = true, description = "정렬 기준, 대여횟수(numberOfBorrowed) or 생성일(createdDate) or 가격순(price)"),
                @Parameter(name = "direction", required = true, description = "정렬 방향, DESC or ASC")
            }
    )
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

    @Operation(summary = "도서 대여", description = "대여할 도서의 id 목록을 통해 도서를 대여합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "도서 대여 성공", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Invalid Token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/books/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> rent(@RequestBody BorrowedRequest request, @AuthenticationPrincipal LoginUser loginUser){
        service.borrowed(request, loginUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
