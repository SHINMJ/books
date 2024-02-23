package com.example.books.infra.auth.config;

import com.example.books.domain.member.Member;
import com.example.books.exception.TokenValidationException;
import com.example.books.infra.auth.dto.LoginRequest;
import com.example.books.infra.auth.dto.LoginUserDetails;
import com.example.books.infra.auth.userdetails.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private AuthenticationManager authenticationManager;

    private TokenProvider tokenProvider;

    Authentication authentication = Mockito.mock(Authentication.class);

    LoginRequest request = new LoginRequest("user@email.com", "1111");

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider("YXZhdGFydHJpcGF1dGhvcml6YXRpb25qd3RtYW5hZ2VzZWNyZXRrZXkK", 6000000, userDetailsService, authenticationManager);
    }

    @Test
    void generateToken() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn("email@email.com");
        when(authentication.getAuthorities())
                .thenReturn(new ArrayList<>());
        when(authentication.isAuthenticated())
                .thenReturn(true);

        String token = tokenProvider.generateToken(request.email(), request.password());

        assertNotNull(token);
        System.out.println(token);
    }

    @Test
    void getAuthentication() {
        Member testUser = Member.of("test@email.com", "1111", "testUser", "010-0000-0000");
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(new LoginUserDetails(testUser));

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn(testUser.getEmail());
        when(authentication.getAuthorities())
                .thenReturn(new ArrayList<>());
        when(authentication.isAuthenticated())
                .thenReturn(true);

        String token = tokenProvider.generateToken(request.email(), request.password());

        assertNotNull(token);

        Authentication authentication = tokenProvider.getAuthentication(token);
        assertEquals(authentication.getName(), "test@email.com");
    }

    @Test
    void validate() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getName())
                .thenReturn("email@email.com");
        when(authentication.getAuthorities())
                .thenReturn(new ArrayList<>());
        when(authentication.isAuthenticated())
                .thenReturn(true);

        String token = tokenProvider.generateToken(request.email(), request.password());

        assertTrue(tokenProvider.validate(token));
        assertThatThrownBy(() -> tokenProvider.validate("12831298jdhfkjsdhfks"))
                .isInstanceOf(TokenValidationException.class);
    }
}