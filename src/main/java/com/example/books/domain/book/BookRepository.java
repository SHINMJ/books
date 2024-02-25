package com.example.books.domain.book;

import com.example.books.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllBy(Pageable pageable);
    Page<Book> findAllByOwner(Member owner, Pageable pageable);
}
