package com.example.blog.repository;

import com.example.blog.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByBlogId(Integer blogId);

    default Image addImage(Image image) {
        return save(image);
    }

    default List<Image> addImages(List<Image> images) {
        return saveAll(images);
    }
}
