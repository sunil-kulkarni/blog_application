package com.example.blog.service;

import com.example.blog.entity.Blog;
import com.example.blog.entity.User;
import com.example.blog.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BlogService {

    private final String UPLOAD_DIR = "./uploads/";

    private final BlogRepository blogRepository;
    private final UserService userService;

    public BlogService(BlogRepository blogRepository, UserService userService) {
        this.blogRepository = blogRepository;
        this.userService = userService;
    }

    public Blog create(Integer userId, String title, String content, MultipartFile imagePath) {
        User user = userService.findById(userId);
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setUser(user);
        
        String savedImagePath = saveImage(imagePath);
        if (savedImagePath != null) {
            blog.setImagePath(savedImagePath);
        }
        
        return blogRepository.save(blog);
    }

    public Blog edit(Integer blogId, String title, String content, MultipartFile imagePath) {
        Blog blog = view(blogId);
        if (title != null) blog.setTitle(title);
        if (content != null) blog.setContent(content);
        
        String savedImagePath = saveImage(imagePath);
        if (savedImagePath != null) {
            // Optional: Delete old image here if needed, keeping it simple for now
            blog.setImagePath(savedImagePath);
        }
        
        return blogRepository.save(blog);
    }
    
    private String saveImage(MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(imageFile.getInputStream(), filePath);

                return filename;
            } catch (IOException e) {
                throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            }
        }
        return null;
    }

    public void delete(Integer blogId) {
        blogRepository.deleteById(blogId);
    }

    public Blog view(Integer blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }
    
    public List<Blog> findAll() {
        return blogRepository.findAll();
    }
    
    public List<Blog> findByUserId(Integer userId) {
        return blogRepository.findByUserId(userId);
    }
}
