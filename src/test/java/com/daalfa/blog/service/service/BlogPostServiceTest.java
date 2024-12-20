package com.daalfa.blog.service.service;

import com.daalfa.blog.service.dto.*;
import com.daalfa.blog.service.exception.NotFoundException;
import com.daalfa.blog.service.mapper.BlogPostMapper;
import com.daalfa.blog.service.model.BlogPost;
import com.daalfa.blog.service.model.Comment;
import com.daalfa.blog.service.repository.BlogPostRepository;
import com.daalfa.blog.service.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BlogPostMapper mapper;

    @InjectMocks
    private BlogPostService blogPostService;

    @Test
    public void givenValidId_whenGetPostById_thenReturnBlogPost() {
        Long postId = 200L;

        var post = new BlogPost();
        post.setId(postId);
        post.setTitle("title");
        post.setContent("content");

        var dto = new BlogPostResponseWithCommentsDTO(
                postId,
                "title",
                "content",
                emptyList()
                );

        when(blogPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(mapper.toBlogPostResponseWithCommentsDTO(post)).thenReturn(dto);

        var result = blogPostService.getPostById(postId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    public void givenValidRequest_whenGetAllPosts_thenReturnBlogPost() {
        Long postId = 200L;

        var post = new BlogPost();
        post.setId(postId);
        post.setTitle("title");
        post.setContent("content");

        var dto = new BlogPostResponseSummaryDTO(
                postId,
                "title",
                "content",
                0
        );

        when(blogPostRepository.findAll()).thenReturn(List.of(post));
        when(mapper.toBlogPostResponseSummaryDTO(post)).thenReturn(dto);

        var result = blogPostService.getAllPosts();

        assertThat(result).isEqualTo(List.of(dto));
    }

    @Test
    public void givenValidPost_whenCreatePost_thenReturnBlogPost() {
        Long postId = 200L;

        var post = new BlogPost();
        post.setId(postId);
        post.setTitle("title");
        post.setContent("content");

        var dtoRequest = new BlogPostRequestDTO("title", "content");

        var dtoResponse = new BlogPostResponseDTO(
                postId,
                "title",
                "content"
        );

        when(mapper.toBlogPostEntity(dtoRequest)).thenReturn(post);
        when(blogPostRepository.save(post)).thenReturn(post);
        when(mapper.toBlogPostResponseDTO(post)).thenReturn(dtoResponse);

        var result = blogPostService.createPost(dtoRequest);

        assertThat(result).isEqualTo(dtoResponse);
    }

    @Test
    public void givenValidComment_whenCreateComment_thenReturnComment() {
        Long postId = 200L;

        var post = new BlogPost();
        post.setId(postId);
        post.setTitle("title");
        post.setContent("content");

        var commentDTO = new CommentDTO("message");

        Comment comment = new Comment();
        comment.setMessage("message");

        when(blogPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(mapper.toCommentEntity(commentDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(mapper.toCommentDTO(comment)).thenReturn(commentDTO);

        var result = blogPostService.createComment(postId, commentDTO);

        assertThat(result).isEqualTo(commentDTO);
    }

    // Negative Tests
    @Test
    public void givenInvalidId_whenGetPostById_thenThrowNotFoundException() {
        Long postId = 0L;

        when(blogPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> blogPostService.getPostById(postId))
                .withMessage("BlogPost not found");
    }

    @Test
    public void givenInvalidPostId_whenCreateComment_thenThrowNotFoundException() {
        Long postId = 0L;
        var commentDTO = new CommentDTO("message");

        when(blogPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> blogPostService.createComment(postId, commentDTO))
                .withMessage("BlogPost not found");
    }
}