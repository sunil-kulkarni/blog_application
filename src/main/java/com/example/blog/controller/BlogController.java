package com.example.blog.controller;

import com.example.blog.entity.Blog;
import com.example.blog.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Blog> create(
            @RequestParam("userId") Integer userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Blog blog = blogService.create(userId, title, content, image);
        return ResponseEntity.ok(blog);
    }

    @PutMapping(value = "/{blogId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Blog> edit(
            @PathVariable Integer blogId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        Blog blog = blogService.edit(blogId, title, content, image);
        return ResponseEntity.ok(blog);
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> delete(@PathVariable Integer blogId) {
        blogService.delete(blogId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Blog> view(@PathVariable Integer blogId) {
        Blog blog = blogService.view(blogId);
        return ResponseEntity.ok(blog);
    }
    
    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
        return ResponseEntity.ok(blogService.findAll());
    }
}
