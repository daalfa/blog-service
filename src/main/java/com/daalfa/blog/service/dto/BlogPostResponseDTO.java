package com.daalfa.blog.service.dto;

public record BlogPostResponseDTO(
        Long id,
        String title,
        String content
) {
}
