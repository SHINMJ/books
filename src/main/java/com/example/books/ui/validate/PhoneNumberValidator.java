package com.example.books.ui.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    private static final String PHONE_NUMBER_REGEX = "^\\d{3}-?\\d{3,4}-?\\d{4}$";
    private static final Pattern PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (PATTERN.matcher(value).matches()){
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                        "휴대폰번호를 형식에 맞게 입력해 주세요.")
                .addConstraintViolation();
        return false;
    }
}
