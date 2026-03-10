package com.example.blog.repository;

import com.example.blog.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    List<Reaction> findByBlogId(Integer blogId);
    Optional<Reaction> findByBlogIdAndUserId(Integer blogId, Integer userId);
}
