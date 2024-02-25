package com.example.books.domain.member;

import com.example.books.domain.book.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

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

    @Test
    void borrowedCascadePersist_success() {
        Member owner = Member.of("owner@email.com", "1111", "owner", "010-9999-2223");
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        entityManager.persist(owner);
        entityManager.persist(book);
        entityManager.persist(member);


        Borrowed borrowed = Borrowed.of(book, member);

        memberRepository.save(member);

        memberRepository.flush();

        Member borrower = memberRepository.findById(this.member.getId()).get();

        assertThat(borrower.getBorrowedList()).contains(borrowed);

        //teardown
        entityManager.remove(book);
    }
}