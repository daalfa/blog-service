package com.daalfa.blog.service.exception;

public record ErrorDetails(
        String field,
        Object rejectedValue,
        String message
) {
}