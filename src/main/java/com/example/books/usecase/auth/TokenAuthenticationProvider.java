package com.example.books.usecase.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public interface TokenAuthenticationProvider {
    String generateToken(String username, String password);
    Authentication getAuthentication(String token);
    boolean validate(String token);
}
