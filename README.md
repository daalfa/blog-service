
# Simple Blog API
This service has a single endpoint `localhost:8080/api/posts`
The following operations are supported:
1. GET `http://localhost:8080/api/posts`
   * This endpoint should return a list of all blog posts,
     including their titles and the number of comments associated with each
     post.
1. GET `http://localhost:8080/api/posts/{id}`
   * Retrieve a specific blog post by its ID, including its
     title, content, and a list of associated comments.
1. POST `http://localhost:8080/api/posts`
   * Create a new blog post.
1. POST `http://localhost:8080/api/posts/{id}/comments`
   * Add a new comment to a specific blog
     post.

Run with `mvn spring-boot:run`

## API

Swagger url: http://localhost:8080/api/swagger-ui/index.html


### 1. Get Blog Post
#### 1.1 Response Payload
```json
{
  "id": 1,
  "title": "My Title",
  "content": "My Content",
  "comments": [
    {
      "message": "My Reply"
    }
  ]
}
```

### 2. Get All Blog Posts
#### 2.1 Response Payload
```json
[
  {
    "id": 1,
    "title": "My Title",
    "content": "My Content",
    "comments": 1
  }
]
```

### 3. Create Blog Post
#### 3.1 Request Payload
```json
{
    "title": "My Title",
    "content": "My Content"
}
```
#### 3.2 Response Payload
```json
{
    "id": 1,
    "title": "My Title",
    "content": "My Content"
}
```

### 4. Create Blog Post Comment
#### 4.1 Request Payload
```json
{
  "message": "My Reply"
}
```
#### 4.2 Response Payload
```json
{
  "message": "My Reply"
}
```

## Validations
* `id` must be positive
* Post `title` must be not empty and between 1 and 64 characters
* Post `content` must be not empty and between 1 and 256 characters
* Comment `message` must be not empty and between 1 and 256 characters

## Architecture
This Spring Boot service was created with [spring initializr](https://start.spring.io/).  
Database is H2 with Liquibase to migrate and changelog.  
Tests with Junit5 and AssertJ.  
Jacoco test coverage is enforced to 90%.

## Next steps
Use a real database such as MySQL or Postgres  
Run tests with TestContainers  
Create Docker image  
Run with Docker Compose or K8s  
Add instrumentation for metrics  
Add Auth  
