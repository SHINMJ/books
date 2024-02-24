package com.example.books.domain.book;

import com.example.books.domain.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @PersistenceContext
    private EntityManager em;

    private Member owner;
    private Member borrower;

    @BeforeEach
    void setUp() {
        owner = Member.of("owner@email.com", "1111", "owner", "010-9999-2223");
        borrower = Member.of("borrower@email.com", "1111", "borrower", "010-9999-2453");
        for (Member member : Arrays.asList(owner, borrower)) {
            em.persist(member);
        }
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();

        for (Member member : Arrays.asList(owner, borrower)) {
            em.remove(member);
        }

        em.clear();
    }

    @Test
    void saved() {
        Book book = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        Book saved = repository.save(book);

        repository.flush();

        Optional<Book> optional = repository.findById(saved.getId());

        assertTrue(optional.isPresent());
    }

    @Test
    void findAllBy_대여많은순() {
        Book book1 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        Book book2 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);
        book1.borrowed();
        book1.returned();

        repository.saveAll(List.of(book1, book2));

        repository.flush();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "numberOfBorrowed"));

        Page<Book> page = repository.findAllBy(pageable);

        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().get(0), book1);
    }

    @Test
    void findAllBy_낮은가격순() {
        Book book1 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(2_000), owner);
        Book book2 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        repository.saveAll(List.of(book1, book2));

        repository.flush();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "price"));

        Page<Book> page = repository.findAllBy(pageable);

        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().get(0), book2);
    }

    @Test
    void findAllBy_최근등록순() {
        Book book1 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(2_000), owner);
        Book book2 = Book.create("첫 1년 움직임의 비밀", "9791186202753", BigDecimal.valueOf(1_000), owner);

        repository.saveAll(List.of(book1, book2));

        repository.flush();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<Book> page = repository.findAllBy(pageable);

        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().get(0), book2);
    }
}