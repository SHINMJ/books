package com.example.books.domain.book;

import com.example.books.domain.common.BaseEntity;
import com.example.books.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Borrowed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Borrowed(Book book, Member member) {
        this.book = book;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Borrowed borrowed = (Borrowed) o;
        return id.equals(borrowed.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
