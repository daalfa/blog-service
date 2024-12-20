package com.daalfa.blog.service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorMessage(
        int status,
        String message,
        List<ErrorDetails> errors
) {

}
