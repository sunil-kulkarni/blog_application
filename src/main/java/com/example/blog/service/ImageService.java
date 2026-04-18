package com.example.blog.service;

import com.example.blog.entity.Image;
import com.example.blog.entity.Blog;
import com.example.blog.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for handling image-related operations.
 */
@Service
public class ImageService {

    // Repository for persisting and retrieving Image entities.
    private final ImageRepository imageRepository;

    // Service used to validate and load Blog entities.
    private final BlogService blogService;

    public ImageService(ImageRepository imageRepository, BlogService blogService) {
        this.imageRepository = imageRepository;
        this.blogService = blogService;
    }

    /**
     * Uploads a new image and associates it with a blog post.
     *
     * @param blogId  the target blog ID
     * @param url     the image URL
     * @param caption the image caption
     * @return the saved Image entity
     */
    public Image upload(Integer blogId, String url, String caption) {
        // Load blog; expected to throw if blog does not exist.
        Blog blog = blogService.view(blogId);

        // Create and populate image entity.
        Image image = new Image();
        image.setUrl(url);
        image.setCaption(caption);
        image.setBlog(blog);

        // Persist image in database.
        return imageRepository.save(image);
    }

    /**
     * Deletes an image by its ID.
     *
     * @param imageId the image ID
     */
    public void delete(Integer imageId) {
        imageRepository.deleteById(imageId);
    }

    /**
     * Returns all images associated with a specific blog.
     *
     * @param blogId the blog ID
     * @return list of images for the blog
     */
    public List<Image> getByBlogId(Integer blogId) {
        return imageRepository.findByBlogId(blogId);
    }
}
