package com.daalfa.blog.service.controller;

import com.daalfa.blog.service.dto.*;
import com.daalfa.blog.service.service.BlogPostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/posts")
@Validated
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogPostResponseWithCommentsDTO getPostById(@PathVariable @Positive Long id) {
        return blogPostService.getPostById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BlogPostResponseSummaryDTO> getAllPosts() {
        return blogPostService.getAllPosts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPostResponseDTO createPost(@Valid @RequestBody BlogPostRequestDTO post) {
        return blogPostService.createPost(post);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createComment(@PathVariable @Positive Long id, @Valid @RequestBody CommentDTO comment) {
        return blogPostService.createComment(id, comment);
    }


}
