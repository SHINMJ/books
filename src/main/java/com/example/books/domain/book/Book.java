package com.example.books.domain.book;

import com.example.books.domain.common.BaseEntity;
import com.example.books.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@ToString
@Getter()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Book extends BaseEntity {
    private static final Long INIT_BORROWED = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String isbn;
    @Column(nullable = false)
    private BigDecimal price;

    private Long numberOfBorrowed;
    private boolean borrowed;

    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "book_member_fk1"), nullable = false)
    private Member owner;

    private Book(String title, String isbn, BigDecimal price, Member owner){
        this.title = title;
        this.isbn = isbn;
        this.price = price;
        this.owner = owner;
        this.numberOfBorrowed = INIT_BORROWED;
        this.borrowed = false;
    }

    public static Book create(String title, String isbn, BigDecimal price, Member owner){
        return new Book(title, isbn, price, owner);
    }

    public Book changePrice(BigDecimal price){
        this.price = price;
        return this;
    }

    public Book borrowed(){
        this.numberOfBorrowed = this.numberOfBorrowed + 1;
        this.borrowed = true;
        return this;
    }

    public Book returned(){
        this.borrowed = false;
        return this;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public boolean isOwner(Member member){
        return this.owner.equals(member);
    }

    public String getOwnerName() {
        return this.owner.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
