package com.example.books.ui.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface PhoneNumber {
    String message() default "phone number is not allow";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
