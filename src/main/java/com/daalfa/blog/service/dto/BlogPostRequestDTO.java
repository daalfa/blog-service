package com.daalfa.blog.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogPostRequestDTO(

        @NotBlank(message = "Title cannot be empty")
        @Size(min = 1, max = 64, message = "Title must be between 1 and 64 characters")
        String title,

        @NotBlank(message = "Content cannot be empty")
        @Size(min = 1, max = 256, message = "Content must be between 1 and 256 characters")
        String content
) {
}
