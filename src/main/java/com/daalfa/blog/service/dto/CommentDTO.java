package com.daalfa.blog.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentDTO(

        @NotBlank(message = "Message cannot be empty")
        @Size(min = 1, max = 256, message = "Message must be between 1 and 256 characters")
        String message
) {
}
