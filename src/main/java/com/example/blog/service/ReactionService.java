package com.example.blog.service;

import com.example.blog.entity.Reaction;
import com.example.blog.entity.Blog;
import com.example.blog.entity.User;
import com.example.blog.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final BlogService blogService;
    private final UserService userService;

    public ReactionService(ReactionRepository reactionRepository, BlogService blogService, UserService userService) {
        this.reactionRepository = reactionRepository;
        this.blogService = blogService;
        this.userService = userService;
    }

    public Reaction addReaction(Integer userId, Integer blogId, String type) {
        User user = userService.findById(userId);
        Blog blog = blogService.view(blogId);
        
        Optional<Reaction> existingReaction = reactionRepository.findByBlogIdAndUserId(blogId, userId);
        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            reaction.setType(type);
            return reactionRepository.save(reaction);
        }

        Reaction reaction = new Reaction();
        reaction.setType(type);
        reaction.setUser(user);
        reaction.setBlog(blog);
        return reactionRepository.save(reaction);
    }

    public void removeReaction(Integer reactionId) {
        reactionRepository.deleteById(reactionId);
    }

    public void toggleLike(Integer userId, Integer blogId) {
        Optional<Reaction> existingReaction = reactionRepository.findByBlogIdAndUserId(blogId, userId);
        if (existingReaction.isPresent()) {
            Reaction reaction = existingReaction.get();
            if ("LIKE".equals(reaction.getType())) {
                reactionRepository.delete(reaction); // Unlike if already liked
            } else {
                reaction.setType("LIKE");
                reactionRepository.save(reaction); // Change reaction to like
            }
        } else {
            addReaction(userId, blogId, "LIKE"); // Add new like
        }
    }
}
