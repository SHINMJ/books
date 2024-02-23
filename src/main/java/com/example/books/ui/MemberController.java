package com.example.books.ui;

import com.example.books.usecase.MemberService;
import com.example.books.usecase.dto.MemberCreateRequest;
import com.example.books.usecase.dto.MemberResponse;
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
public class MemberController {
    private final MemberService service;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity signup(@Valid @RequestBody MemberCreateRequest request){
        MemberResponse response = service.createMember(request);
        return ResponseEntity.created(URI.create("/member/"+response.id())).build();
    }
}
