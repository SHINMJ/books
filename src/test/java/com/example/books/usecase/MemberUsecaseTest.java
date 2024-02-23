package com.example.books.usecase;

import com.example.books.domain.member.Member;
import com.example.books.domain.member.MemberRepository;
import com.example.books.exception.AlreadyExistsEmailException;
import com.example.books.exception.MemberNotFoundException;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.member.dto.MemberUpdateRequest;
import com.example.books.usecase.member.dto.MemberCreateRequest;
import com.example.books.usecase.member.dto.MemberResponse;
import com.example.books.usecase.member.MemberUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberUsecaseTest {

    @Mock
    private MemberRepository repository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MemberUsecase service;

    private Member member = Member.of("test@email.com", "1111", "testUser", "010-9999-2222");

    private LoginUser loginUser = mock(LoginUser.class);

    @BeforeEach
    void setUp() {
        service = new MemberUsecase(repository, passwordEncoder);
    }

    @Test
    void create_success() {
        MemberCreateRequest request = new MemberCreateRequest(member.getEmail(), member.getPassword(), member.getName(), member.getPhone());

        when(repository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Member.class)))
                .thenReturn(member);

        MemberResponse response = service.createMember(request);

        assertEquals(response.name(), member.getName());

    }

    @Test
    void create_failed_alreadyExistsEmail() {
        MemberCreateRequest request = new MemberCreateRequest(member.getEmail(), member.getPassword(), member.getName(), member.getPhone());

        when(repository.findByEmail(anyString()))
                .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.createMember(request))
                .isInstanceOf(AlreadyExistsEmailException.class);
    }

    @Test
    void findResponseById_failed_notExists() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findResponseById(1L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void changeMemberInfo_success() {
        MemberUpdateRequest request = new MemberUpdateRequest("changePw", "changeName", member.getPhone());

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(member));
        when(loginUser.getId())
                .thenReturn(1L);
        when(loginUser.getEmail())
                .thenReturn(member.getEmail());
        when(repository.save(any()))
                .thenReturn(member);

        MemberResponse response = service.changeInfo(request, loginUser);

        assertEquals(response.email(), "test@email.com");
        assertEquals(response.name(), "changeName");
    }
}