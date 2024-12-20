package com.daalfa.blog.service.mapper;

import com.daalfa.blog.service.dto.*;
import com.daalfa.blog.service.model.BlogPost;
import com.daalfa.blog.service.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogPostMapper {

    BlogPostResponseWithCommentsDTO toBlogPostResponseWithCommentsDTO(BlogPost entity);

    @Mapping(target = "comments", expression = "java(entity.getComments().size())")
    BlogPostResponseSummaryDTO toBlogPostResponseSummaryDTO(BlogPost entity);

    BlogPostResponseDTO toBlogPostResponseDTO(BlogPost entity);

    BlogPost toBlogPostEntity(BlogPostRequestDTO dto);

    CommentDTO toCommentDTO(Comment entity);

    Comment toCommentEntity(CommentDTO dto);
}
