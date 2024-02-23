package com.example.books.ui.validate;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PasswordValidatorTest {

    PasswordValidator passwordValidator = new PasswordValidator();
    ConstraintValidatorContext context = mock();

    /**
     * - 최소 6자 이상 10자 이하
     * - 영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요
     */
    @ParameterizedTest
    @CsvSource(value = {"123ds:false", "dsefsgs:false", "DSsdfFE221:true", "EWERWERD:false", "!@#DSES:false", "difiDDEsf:true", "ewd123$:true"}, delimiter = ':')
    void validPassword(String input, boolean expected) {

        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock());

        boolean valid = passwordValidator.isValid(input, context);

        assertThat(valid).isEqualTo(expected);
    }
}