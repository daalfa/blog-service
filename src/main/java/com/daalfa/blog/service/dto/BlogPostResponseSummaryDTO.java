package com.daalfa.blog.service.dto;

public record BlogPostResponseSummaryDTO(
        Long id,
        String title,
        String content,
        int comments
) {
}
