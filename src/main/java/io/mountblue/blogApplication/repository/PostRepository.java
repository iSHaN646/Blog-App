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
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.author IS NOT NULL")
    List<String> findAllDistinctAuthors();

    @Query("""
    SELECT p FROM Post p
    LEFT JOIN p.postTags pt
    LEFT JOIN pt.tag t
    WHERE (:search IS NULL OR LOWER(p.title) LIKE %:search% OR LOWER(p.content) LIKE %:search%
     OR LOWER(p.excerpt) LIKE %:search% OR LOWER(p.author) LIKE %:search%
     OR LOWER(t.name) LIKE %:search%)
    AND (CAST(:fromDateTime as timestamp) IS NULL OR p.publishedAt >= :fromDateTime)
    AND (CAST(:toDateTime as timestamp)  IS NULL OR p.publishedAt <= :toDateTime)
    AND (:authorNames IS NULL OR p.author IN :authorNames)
    AND (:tagIds IS NULL OR t.id IN :tagIds)
    GROUP BY p.id
    HAVING (:tagIds IS NULL OR COUNT(DISTINCT t.id) = :tagCount)
""")
    Page<Post> findFilteredPostsWithAllTags(
            @Param("authorNames") List<String> authorNames,
            @Param("tagIds") List<Long> tagIds,
            @Param("tagCount") int tagCount,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("search") String search,
            Pageable pageable
    );

}
