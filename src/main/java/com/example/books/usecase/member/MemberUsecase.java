package com.example.books.usecase.member;

import com.example.books.domain.member.Member;
import com.example.books.domain.member.MemberRepository;
import com.example.books.exception.AlreadyExistsEmailException;
import com.example.books.exception.MemberNotFoundException;
import com.example.books.usecase.auth.dto.LoginUser;
import com.example.books.usecase.member.dto.MemberUpdateRequest;
import com.example.books.usecase.member.dto.MemberCreateRequest;
import com.example.books.usecase.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberUsecase {

    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse createMember(MemberCreateRequest request){

        Optional<Member> exists = repository.findByEmail(request.email());
        if (exists.isPresent()){
            throw new AlreadyExistsEmailException();
        }

        Member member = request.toEntity();
        member.changePassword(passwordEncoder.encode(request.password()));

        Member saved = repository.save(member);
        return MemberResponse.of(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse findResponseById(Long id) {
        Member member = findById(id);
        return MemberResponse.of(member);
    }

    public MemberResponse changeInfo(MemberUpdateRequest request, LoginUser loginUser) {
        Member member = findById(loginUser.getId());

        member.changeInfo(request.toEntity());
        member.changePassword(passwordEncoder.encode(request.password()));

        Member saved = repository.save(member);
        return MemberResponse.of(saved);
    }

    private Member findById(Long id){
        return repository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

}
