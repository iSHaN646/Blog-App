package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    void createPost(Post post,String newTags);

    void deletePost(Long id);

    void updatePost(Long id,Post post,String newTags);

    Post getPost(Long id);

    Page<Post> getPaginatedPosts(Pageable pageable);

    Page<Post> getPaginatedPostsByAuthor(String authorName, Pageable pageable);

    List<String> getAllDistinctAuthors();

    Page<Post> getPaginatedPostsByAuthorAndTags(String authorName, List<Long> tagIds, Pageable pageable);

    Page<Post> getPaginatedPostsByTags(List<Long> tagId, Pageable pageable);

    Page<Post> getPostsByAuthorTagsAndDateRange(String author, List<Long> tagIds, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Post> getPostsByAuthorAndDateRange(String author, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Post> getPostsByTagsAndDateRange(List<Long> tagIds, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Post> getPostsByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);

}
