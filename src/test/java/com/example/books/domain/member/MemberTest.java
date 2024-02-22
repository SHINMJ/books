package com.example.books.domain.member;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    private Member member = Member.of("test@email.com", "1111", "testUser", "010-9999-2222");

    @Test
    void changePassword() {
        String other = "1234";

        assertNotEquals(member.getPassword(), other);

        member.changePassword(other);

        assertEquals(member.getPassword(), other);
    }

    @Test
    void changeInfo() {
        Member other = Member.of(member.getEmail(), "1234", "changeUser", "010-2222-1232");

        member.changeInfo(other);

        assertAll(
                () -> assertEquals(member.getEmail(), "test@email.com"),
                () -> assertNotEquals(member.getName(), "testUser"),
                () -> assertNotEquals(member.getPhone(), "010-9999-2222")
        );

    }
}