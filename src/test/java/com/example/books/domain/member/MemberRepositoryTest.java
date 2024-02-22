package com.example.books.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.of("test@email.com", "1111", "testUser", "010-9999-9999");
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        memberRepository.flush();
    }

    @Test
    void createMember_success() {

        Member saved = memberRepository.save(member);

        //query
        memberRepository.flush();

        assertEquals(saved, member);
    }

    @Test
    void findByEmail_success() {
        //given
        memberRepository.save(member);
        memberRepository.flush();

        //when
        Optional<Member> memberOptional = memberRepository.findByEmail(member.getEmail());

        //then
        assertTrue(memberOptional.isPresent());
        assertEquals(memberOptional.get(), member);
    }

    @Test
    void findByEmail_failed_notExistsEmail() {
        //when
        Optional<Member> memberOptional = memberRepository.findByEmail(member.getEmail());

        assertFalse(memberOptional.isPresent());
    }
}