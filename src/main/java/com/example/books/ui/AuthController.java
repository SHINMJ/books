package com.example.books.ui;

import com.example.books.exception.dto.ErrorResponse;
import com.example.books.usecase.auth.AuthUsecase;
import com.example.books.usecase.member.MemberUsecase;
import com.example.books.usecase.auth.dto.LoginRequest;
import com.example.books.usecase.member.dto.MemberCreateRequest;
import com.example.books.usecase.member.dto.MemberResponse;
import com.example.books.usecase.auth.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "Authentication API")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthUsecase service;

    @Operation(summary = "회원가입", description = "사용자 정보를 통해 회원가입을 합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 정보",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity signup(@Valid @RequestBody MemberCreateRequest request){
        MemberResponse response = service.signup(request);
        return ResponseEntity.created(URI.create("/member/"+response.id())).build();
    }

    @Operation(summary = "로그인", description = "사용자 정보를 통해 로그인을 합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "잘못된 입력 정보",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request){
        TokenResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

}
