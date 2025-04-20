package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Tag;

import java.util.Set;

public interface PostTagService {

    void addTagToPost(Long postId, Long tagId);
    Set<Tag> getTagsByPostId(Long postId);
    void removeAllTagsFromPost(Long postId);
}
