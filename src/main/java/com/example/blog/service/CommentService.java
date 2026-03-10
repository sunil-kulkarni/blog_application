package com.example.blog.service;

import com.example.blog.entity.Comment;
import com.example.blog.entity.Blog;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final BlogService blogService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, BlogService blogService, UserService userService) {
        this.commentRepository = commentRepository;
        this.blogService = blogService;
        this.userService = userService;
    }

    public Comment add(Integer userId, Integer blogId, String content) {
        User user = userService.findById(userId);
        Blog blog = blogService.view(blogId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setBlog(blog);
        return commentRepository.save(comment);
    }

    public Comment edit(Integer commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void delete(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByBlogId(Integer blogId) {
        return commentRepository.findByBlogId(blogId);
    }
}
