package com.example.books.exception;

import com.example.books.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    protected ResponseEntity<ErrorResponse> handleUnprocessableEntityException(HttpClientErrorException.UnprocessableEntity e){
        return errorResponse(ErrorResponse.from("Unprocessable Entity"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return errorResponse(ErrorResponse.of("Method Not Allowed", HttpStatus.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return errorResponse(ErrorResponse.from(e.getName()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<ErrorResponse.ValidationFieldError> fieldErrors = ErrorResponse.ValidationFieldError.of(e.getBindingResult());
        return errorResponse(ErrorResponse.of("입력된 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST, fieldErrors));
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e){
        return errorResponse(ErrorResponse.of(e.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        return errorResponse(ErrorResponse.of("Unauthorized", HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(TokenValidationException.class)
    protected ResponseEntity<ErrorResponse> handleTokenValidationException(TokenValidationException e) {
        return errorResponse(ErrorResponse.of(e.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(BizException.class)
    protected ResponseEntity<ErrorResponse> handleBizException(BizException e) {
        return errorResponse(ErrorResponse.from(e.getMessage()));
    }

    private ResponseEntity<ErrorResponse> errorResponse(final ErrorResponse response){
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
