package com.daalfa.blog.service.it;

import com.daalfa.blog.service.dto.*;
import com.daalfa.blog.service.exception.ErrorMessage;
import com.daalfa.blog.service.model.BlogPost;
import com.daalfa.blog.service.repository.BlogPostRepository;
import com.daalfa.blog.service.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogPostIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setup() {
        blogPostRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void shouldGetSinglePost() {
        BlogPost post = new BlogPost();
        post.setTitle("Title");
        post.setContent("Content");

        var id = blogPostRepository.save(post).getId();

        ResponseEntity<BlogPostResponseWithCommentsDTO> response =
                restTemplate.getForEntity("/posts/"+id,
                        BlogPostResponseWithCommentsDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody().comments()).isEmpty();
        assertThat(response.getBody().id()).isEqualTo(post.getId());
    }

    @Test
    void shouldGetAllPosts() {
        BlogPost post1 = new BlogPost();
        post1.setTitle("Title 1");
        post1.setContent("Content 1");

        BlogPost post2 = new BlogPost();
        post2.setTitle("Title 1");
        post2.setContent("Content 1");

        blogPostRepository.save(post1);
        blogPostRepository.save(post2);

        ResponseEntity<BlogPostResponseSummaryDTO[]> response =
                restTemplate.getForEntity(
                        "/posts",
                        BlogPostResponseSummaryDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldCreatePost() {
        var blogPostRequestDTO = new BlogPostRequestDTO("title", "content");

        ResponseEntity<BlogPostResponseDTO> response =
                restTemplate.postForEntity(
                        "/posts",
                        blogPostRequestDTO,
                        BlogPostResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(response.getBody().title()).isEqualTo("title");
        assertThat(response.getBody().content()).isEqualTo("content");

        Optional<BlogPost> savedPost = blogPostRepository.findById(response.getBody().id());
        assertThat(savedPost).isPresent();
        assertThat(savedPost.get().getTitle()).isEqualTo("title");
        assertThat(savedPost.get().getContent()).isEqualTo("content");
    }

    @Test
    void shouldCreatePostComment() {
        BlogPost post = new BlogPost();
        post.setTitle("Title");
        post.setContent("Content");

        var id = blogPostRepository.save(post).getId();

        CommentDTO commentDTO = new CommentDTO("comment");

        ResponseEntity<CommentDTO> response =
                restTemplate.postForEntity(
                        "/posts/"+id+"/comments",
                        commentDTO,
                        CommentDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().message()).isEqualTo("comment");

        ResponseEntity<BlogPostResponseWithCommentsDTO> response2 =
                restTemplate.getForEntity("/posts/"+id,
                        BlogPostResponseWithCommentsDTO.class);

        assertThat(response2.getBody().comments()).hasSize(1);
        assertThat(response2.getBody().comments().get(0).message()).isEqualTo("comment");
    }

    @Test
    void givenNegativeId_whenGetPost_thenShouldReturnBadRequest() {
        long invalidId = -1;

        ResponseEntity<ErrorMessage> response =
                restTemplate.getForEntity(
                        "/posts/" + invalidId,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSize(1);
        assertThat(response.getBody().errors().get(0).rejectedValue()).isEqualTo(-1);
        assertThat(response.getBody().errors().get(0).message()).isEqualTo("must be greater than 0");
    }

    // Create Post - Title Validation
    @Test
    void givenLongTitle_whenCreatePost_thenShouldReturnBadRequest() {
        String longTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod";

        var blogPostRequestDTO = new BlogPostRequestDTO(
                longTitle,
                "content");

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts",
                        blogPostRequestDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSize(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo(longTitle);
                    assertThat(error.message()).isEqualTo("Title must be between 1 and 64 characters");
                });
    }

    @Test
    void givenEmptyTitle_whenCreatePost_thenShouldReturnBadRequest() {
        var blogPostRequestDTO = new BlogPostRequestDTO(
                "",
                "content");

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts",
                        blogPostRequestDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSizeGreaterThanOrEqualTo(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo("");
                    assertThat(error.message()).isEqualTo("Title cannot be empty");
                });
    }

    // Create Post - Content Validation
    @Test
    void givenLongContent_whenCreatePost_thenShouldReturnBadRequest() {
        String longContent = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
                labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
                laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit
                """;

        var blogPostRequestDTO = new BlogPostRequestDTO(
                "title",
                longContent);

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts",
                        blogPostRequestDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSize(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo(longContent);
                    assertThat(error.message()).isEqualTo("Content must be between 1 and 256 characters");
                });
    }

    @Test
    void givenEmptyContent_whenCreatePost_thenShouldReturnBadRequest() {
        var blogPostRequestDTO = new BlogPostRequestDTO(
                "title",
                "");

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts",
                        blogPostRequestDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSizeGreaterThanOrEqualTo(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo("");
                    assertThat(error.message()).isEqualTo("Content cannot be empty");
                });
    }

    // Create Comment - Message Validation
    @Test
    void givenLongComment_whenCreateComment_thenShouldReturnBadRequest() {
        BlogPost post = new BlogPost();
        post.setTitle("Title");
        post.setContent("Content");
        var id = blogPostRepository.save(post).getId();

        String longMessage = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
                labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
                laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit
                """;

        var commentDTO = new CommentDTO(longMessage);

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts/"+id+"/comments",
                        commentDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSize(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo(longMessage);
                    assertThat(error.message()).isEqualTo("Message must be between 1 and 256 characters");
                });
    }

    @Test
    void givenEmptyComment_whenCreateComment_thenShouldReturnBadRequest() {
        BlogPost post = new BlogPost();
        post.setTitle("Title");
        post.setContent("Content");
        var id = blogPostRepository.save(post).getId();

        var commentDTO = new CommentDTO("");

        ResponseEntity<ErrorMessage> response =
                restTemplate.postForEntity(
                        "/posts/"+id+"/comments",
                        commentDTO,
                        ErrorMessage.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().errors()).hasSizeGreaterThanOrEqualTo(1)
                .anySatisfy(error -> {
                    assertThat(error.rejectedValue()).isEqualTo("");
                    assertThat(error.message()).isEqualTo("Message cannot be empty");
                });
    }
}
