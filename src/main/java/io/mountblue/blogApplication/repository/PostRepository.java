package io.mountblue.blogApplication.repository;

import io.mountblue.blogApplication.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByAuthor(String authorName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt " +
            "WHERE p.author = :author " +
            "AND pt.tag.id IN :tagIds " +
            "AND p.publishedAt BETWEEN :from AND :to")
    Page<Post> findByAuthorAndTagsAndPublishedAtBetween(
            @Param("author") String author,
            @Param("tagIds") List<Long> tagIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.publishedAt BETWEEN :from AND :to")
    Page<Post> findByPublishedAtBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);


    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt " +
            "WHERE pt.tag.id IN :tagIds " +
            "AND p.publishedAt BETWEEN :from AND :to")
    Page<Post> findByTagsAndPublishedAtBetween(
            @Param("tagIds") List<Long> tagIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);


    @Query("SELECT p FROM Post p " +
            "WHERE p.author = :author " +
            "AND p.publishedAt BETWEEN :from AND :to")
    Page<Post> findByAuthorAndPublishedAtBetween(
            @Param("author") String author,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);


    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.author IS NOT NULL")
    List<String> findAllDistinctAuthors();

    @Query("SELECT p FROM Post p JOIN p.postTags pt WHERE p.author = :authorName AND pt.tag.id IN :tagIds")
    Page<Post> findByAuthorAndTagsIn(String authorName, List<Long> tagIds, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt WHERE pt.tag.id IN :tagIds")
    Page<Post> findDistinctByTagsIn(List<Long> tagIds, Pageable pageable);

}
