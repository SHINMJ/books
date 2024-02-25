package com.example.books.domain.member;

import com.example.books.domain.book.Book;
import com.example.books.domain.common.BaseEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "borrowed_book_fk"))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", foreignKey = @ForeignKey(name = "borrowed_member_fk"))
    private Member borrower;

    private Borrowed(Book book, Member member) {
        this.book = book;
        this.borrower = member;
        member.addBorrowedBooks(this);
    }

    public static Borrowed of(Book book, Member member){
        return new Borrowed(book, member);
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
