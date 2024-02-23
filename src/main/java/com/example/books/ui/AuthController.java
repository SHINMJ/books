package com.example.books.ui;

import com.example.books.usecase.auth.AuthUsecase;
import com.example.books.usecase.member.MemberUsecase;
import com.example.books.usecase.auth.dto.LoginRequest;
import com.example.books.usecase.member.dto.MemberCreateRequest;
import com.example.books.usecase.member.dto.MemberResponse;
import com.example.books.usecase.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthUsecase service;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity signup(@Valid @RequestBody MemberCreateRequest request){
        MemberResponse response = service.signup(request);
        return ResponseEntity.created(URI.create("/member/"+response.id())).build();
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request){
        TokenResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }



}
