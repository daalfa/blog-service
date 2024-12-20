package com.daalfa.blog.service.dto;

import java.util.List;

public record BlogPostResponseWithCommentsDTO(
        Long id,
        String title,
        String content,
        List<CommentDTO> comments
) {
}
