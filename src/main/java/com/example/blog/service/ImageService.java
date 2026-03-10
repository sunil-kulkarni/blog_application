package com.example.blog.service;

import com.example.blog.entity.Image;
import com.example.blog.entity.Blog;
import com.example.blog.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final BlogService blogService;

    public ImageService(ImageRepository imageRepository, BlogService blogService) {
        this.imageRepository = imageRepository;
        this.blogService = blogService;
    }

    public Image upload(Integer blogId, String url, String caption) {
        Blog blog = blogService.view(blogId);
        Image image = new Image();
        image.setUrl(url);
        image.setCaption(caption);
        image.setBlog(blog);
        return imageRepository.save(image);
    }

    public void delete(Integer imageId) {
        imageRepository.deleteById(imageId);
    }

    public List<Image> getByBlogId(Integer blogId) {
        return imageRepository.findByBlogId(blogId);
    }
}
