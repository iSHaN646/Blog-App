package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    void createPost(Post post,String newTags);

    void deletePost(Long id);

    void updatePost(Long id,Post post,String newTags);

    Post getPost(Long id);

    List<String> getAllDistinctAuthors();
    Page<Post> getPaginatedPosts(Pageable pageable);
     Page<Post> getFilteredPosts(
            List<String> authorNames,
            List<Long> tagIds,
            LocalDate from,
            LocalDate to,
            String search,
            Pageable pageable
    );
}
