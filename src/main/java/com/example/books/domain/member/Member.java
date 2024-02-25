package com.example.books.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL)
    private List<Borrowed> borrowedList;

    private Member(String email, String password, String name, String phone){
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.borrowedList = new ArrayList<>();
    }

    public static Member of(String email, String password, String name, String phone){
        return new Member(email, password, name, phone);
    }

    public static Member of(String name, String phone){
        return new Member("", "", name, phone);
    }

    public Member changePassword(String password){
        this.password = password;
        return this;
    }

    public Member changeInfo(Member info){
        this.name = info.name;
        this.phone = info.phone;
        return this;
    }

    public void addBorrowedBooks(Borrowed borrowed){
        this.borrowedList.add(borrowed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
