package com.daalfa.blog.service.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(NotFoundException ex) {
        int errorCode = HttpStatus.NOT_FOUND.value();
        log.error("handleNotFoundException: {}", ex.getMessage());
        return new ErrorMessage(errorCode, ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorMessage handleInvalidPayloadException(HttpMessageNotReadableException ex) {
        int errorCode = HttpStatus.NOT_FOUND.value();
        log.error("handleInvalidPayloadException: {}", ex.getMessage());
        return new ErrorMessage(errorCode, "Request body is missing or malformed", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationExceptions(MethodArgumentNotValidException ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        log.error("handleValidationExceptions: {}", ex.getMessage());

        List<ErrorDetails> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorDetails(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .toList();

        return new ErrorMessage(errorCode, "Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException ex) {
        int errorCode = HttpStatus.BAD_REQUEST.value();
        log.error("handleConstraintViolationException: {}", ex.getMessage());

        List<ErrorDetails> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorDetails(
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .toList();

        return new ErrorMessage(errorCode, "Validation failed", errors);
    }
}