package com.example.books.infra.auth.config;

import com.example.books.exception.MemberNotFoundException;
import com.example.books.exception.TokenValidationException;
import com.example.books.usecase.auth.TokenAuthenticationProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class TokenProvider implements TokenAuthenticationProvider {
    private static final String AUTHORITIES_KEY = "auth";

    private final String tokenSecret;
    private final long expiredIn;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    private Key key;

    public TokenProvider(String tokenSecret, long expiredIn, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.tokenSecret = tokenSecret;
        this.expiredIn = expiredIn;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        byte[] bytes = Decoders.BASE64.decode(this.tokenSecret);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String username, String password){

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (!authentication.isAuthenticated()){
            throw new MemberNotFoundException();
        }

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.expiredIn);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public boolean validate(String token){
        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 서명입니다.");
            throw new TokenValidationException("잘못된 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰 입니다.");
            throw new TokenValidationException("만료된 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다.");
            throw new TokenValidationException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰이 잘못되었습니다.");
            throw new TokenValidationException("토큰이 잘못되었습니다.");
        }
    }
}
