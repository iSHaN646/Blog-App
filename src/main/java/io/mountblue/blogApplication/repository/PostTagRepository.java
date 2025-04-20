package io.mountblue.blogApplication.repository;

import io.mountblue.blogApplication.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag,Long> {
    List<PostTag> findByPostId(Long postId);
}
