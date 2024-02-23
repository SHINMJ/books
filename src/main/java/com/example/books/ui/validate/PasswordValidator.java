package com.example.books.ui.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.regex.Pattern;

@Component
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_SIZE = 6;
    private static final int MAX_SIZE = 10;
    //영문 소문자, 대문자, 숫자 중 최소 두 가지 이상 조합 필요
    private static final Pattern PATTERN_LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern PATTERN_UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern PATTERN_DIGIT = Pattern.compile("[0-9]");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (matches(value)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        MessageFormat.format("{0}자 이상의 {1}자 이하의 숫자, 영문자를 포함한 비밀번호를 입력해주세요", MIN_SIZE, MAX_SIZE))
                .addConstraintViolation();
        return false;
    }

    private boolean matches(String value){
        if (value.length() < MIN_SIZE || value.length() > MAX_SIZE){
            return false;
        }

        boolean containsLowercase = PATTERN_LOWER_CASE.matcher(value).find();
        boolean containsUppercase = PATTERN_UPPER_CASE.matcher(value).find();
        boolean containsDigit = PATTERN_DIGIT.matcher(value).find();

        return (containsLowercase ? 1 : 0) + (containsUppercase ? 1 : 0) + (containsDigit ? 1 : 0) >= 2;
    }

}
