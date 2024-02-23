package com.example.books.ui.validate;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator = new PhoneNumberValidator();
    private ConstraintValidatorContext context = mock();

    @ParameterizedTest
    @CsvSource(value = {"010-dfsd-eeef:false", "010-9999-2321:true", "111:false", "0102938821:true", "01001012:false", "01-23820122:false", "010-29328192:true"}, delimiter = ':')
    void validPassword(String input, boolean expected) {

        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(mock());

        boolean valid = validator.isValid(input, context);

        assertThat(valid).isEqualTo(expected);
    }
}