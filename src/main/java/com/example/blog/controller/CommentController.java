package com.example.blog.controller;

import com.example.blog.entity.Comment;
import com.example.blog.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    public record AddCommentRequest(Integer userId, Integer blogId, String content) {}
    public record EditCommentRequest(String content) {}

    @PostMapping
    public ResponseEntity<Comment> add(@RequestBody AddCommentRequest request) {
        Comment comment = commentService.add(request.userId(), request.blogId(), request.content());
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> edit(@PathVariable Integer commentId, @RequestBody EditCommentRequest request) {
        Comment comment = commentService.edit(commentId, request.content());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<Comment>> getByBlogId(@PathVariable Integer blogId) {
        return ResponseEntity.ok(commentService.getCommentsByBlogId(blogId));
    }
}
