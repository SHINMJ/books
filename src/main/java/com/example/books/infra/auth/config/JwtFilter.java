package com.example.books.infra.auth.config;

import com.example.books.exception.TokenValidationException;
import com.example.books.usecase.auth.TokenAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String[] PERMIT_PATTERNS = {"/auth/signup", "/auth/login"};
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenAuthenticationProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (Arrays.stream(PERMIT_PATTERNS).anyMatch(pattern -> request.getServletPath().contains(pattern))){
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (token == null){
            sendError(response, "토큰이 없습니다.");
            return;
        }

        try {
            setAuthentication(token);
        }catch (TokenValidationException e) {
            sendError(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }


    private void setAuthentication(String token){
        if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private String resolveToken(HttpServletRequest request){
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)){
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
