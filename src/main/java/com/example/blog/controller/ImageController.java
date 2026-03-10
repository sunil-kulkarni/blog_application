package com.example.blog.controller;

import com.example.blog.entity.Image;
import com.example.blog.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    public record UploadImageRequest(Integer blogId, String url, String caption) {}

    @PostMapping
    public ResponseEntity<Image> upload(@RequestBody UploadImageRequest request) {
        Image image = imageService.upload(request.blogId(), request.url(), request.caption());
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Integer imageId) {
        imageService.delete(imageId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<List<Image>> getByBlogId(@PathVariable Integer blogId) {
        return ResponseEntity.ok(imageService.getByBlogId(blogId));
    }
}
