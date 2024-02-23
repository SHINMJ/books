package com.example.books.exception.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private static final String DEFAULT_ERROR_MESSAGE = "ERROR!!";

    private String message;
    private int statusCode;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private List<ValidationFieldError> fieldErrors;

    private ErrorResponse(final String message){
        this.message = message;
        this.statusCode = HttpStatus.BAD_REQUEST.value();
        this.status = HttpStatus.BAD_REQUEST;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = new ArrayList<>();
    }

    private ErrorResponse(final String message, final HttpStatus status){
        this.message = message;
        this.statusCode = status.value();
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = new ArrayList<>();

    }

    private ErrorResponse(final String message, final HttpStatus status, final List<ValidationFieldError> errors){
        this.message = message;
        this.statusCode = status.value();
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = new ArrayList<>(errors);
    }

    public static ErrorResponse from(String message){
        return new ErrorResponse(message);
    }

    public static ErrorResponse of(String message, HttpStatus status){
        return new ErrorResponse(message, status);
    }

    public static ErrorResponse of(String message, HttpStatus status, List<ValidationFieldError> errors){
        return new ErrorResponse(message, status, errors);
    }

    public record ValidationFieldError (String field, String rejected, String message) {

        public static List<ValidationFieldError> of(final BindingResult bindingResult){
            return bindingResult.getFieldErrors().stream()
                    .map(fieldError -> new ValidationFieldError(
                            fieldError.getField(),
                            fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue().toString(),
                            fieldError.getDefaultMessage()))
                    .toList();
        }
    }
}
