package com.daalfa.blog.service.service;

import com.daalfa.blog.service.dto.*;
import com.daalfa.blog.service.exception.NotFoundException;
import com.daalfa.blog.service.mapper.BlogPostMapper;
import com.daalfa.blog.service.model.BlogPost;
import com.daalfa.blog.service.model.Comment;
import com.daalfa.blog.service.repository.BlogPostRepository;
import com.daalfa.blog.service.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;
    private final BlogPostMapper mapper;

    public BlogPostService(BlogPostRepository blogPostRepository,
                           CommentRepository commentRepository,
                           BlogPostMapper mapper) {
        this.blogPostRepository = blogPostRepository;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
    }

    public BlogPostResponseWithCommentsDTO getPostById(Long id) {
        log.info("Get post by id: {}", id);
        return blogPostRepository.findById(id)
                .map(mapper::toBlogPostResponseWithCommentsDTO)
                .orElseThrow(() -> new NotFoundException("BlogPost not found"));
    }

    public List<BlogPostResponseSummaryDTO> getAllPosts() {
        log.info("Get all posts");
        return blogPostRepository.findAll().stream()
                .map(mapper::toBlogPostResponseSummaryDTO)
                .toList();
    }

    public BlogPostResponseDTO createPost(BlogPostRequestDTO post) {
        log.info("Create post: {}", post);
        return mapper.toBlogPostResponseDTO(
                blogPostRepository.save(mapper.toBlogPostEntity(post))
        );
    }

    @Transactional
    public CommentDTO createComment(Long id, CommentDTO commentDTO) {
        log.info("Create comment: {}", commentDTO);
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("BlogPost not found"));

        Comment comment = mapper.toCommentEntity(commentDTO);
        comment.setBlogPost(post);
        comment = commentRepository.save(comment);

        post.getComments().add(comment);
        return mapper.toCommentDTO(comment);
    }
}
