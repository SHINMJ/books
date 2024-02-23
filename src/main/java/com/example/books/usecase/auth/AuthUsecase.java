package com.example.books.usecase.auth;

import com.example.books.usecase.auth.dto.LoginRequest;
import com.example.books.usecase.auth.dto.TokenResponse;
import com.example.books.usecase.member.MemberUsecase;
import com.example.books.usecase.member.dto.MemberCreateRequest;
import com.example.books.usecase.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthUsecase {

    private final MemberUsecase memberUsecase;
    private final TokenAuthenticationProvider tokenProvider;

    public MemberResponse signup(MemberCreateRequest request) {
        return memberUsecase.createMember(request);
    }

    public TokenResponse login(LoginRequest request){
        String token = tokenProvider.generateToken(request.email(), request.password());
        return TokenResponse.valueOf(token);
    }

}
