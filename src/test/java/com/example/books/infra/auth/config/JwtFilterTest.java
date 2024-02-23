package com.example.books.infra.auth.config;

import com.example.books.exception.TokenValidationException;
import com.example.books.usecase.auth.TokenAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private TokenAuthenticationProvider tokenProvider;

    @InjectMocks
    private JwtFilter jwtFilter;

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    @Test
    void permit_path() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/auth/signup");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verifyNoMoreInteractions(filterChain);

    }

    @Test
    void token_null() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/authenticatePath");
        when(request.getHeader(anyString()))
                .thenReturn("");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");
    }

    @Test
    void verifyToken_setAuthentication() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/authenticatePath");
        when(request.getHeader(anyString()))
                .thenReturn("Bearer token");
        when(tokenProvider.validate(anyString()))
                .thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void notValidToken() throws ServletException, IOException {
        when(request.getServletPath())
                .thenReturn("/authenticatePath");
        when(request.getHeader(anyString()))
                .thenReturn("Bearer token");
        when(tokenProvider.validate(anyString()))
                .thenThrow(new TokenValidationException("잘못된 토큰"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 토큰");
    }
}