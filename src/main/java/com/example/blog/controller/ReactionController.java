package com.example.blog.controller;

import com.example.blog.entity.Reaction;
import com.example.blog.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    public record AddReactionRequest(Integer userId, Integer blogId, String type) {}

    @PostMapping
    public ResponseEntity<Reaction> addReaction(@RequestBody AddReactionRequest request) {
        Reaction reaction = reactionService.addReaction(request.userId(), request.blogId(), request.type());
        return ResponseEntity.ok(reaction);
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<Void> removeReaction(@PathVariable Integer reactionId) {
        reactionService.removeReaction(reactionId);
        return ResponseEntity.ok().build();
    }
}
