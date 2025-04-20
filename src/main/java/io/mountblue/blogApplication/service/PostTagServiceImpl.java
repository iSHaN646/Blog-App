package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.entity.PostTag;
import io.mountblue.blogApplication.entity.Tag;
import io.mountblue.blogApplication.repository.PostRepository;
import io.mountblue.blogApplication.repository.PostTagRepository;
import io.mountblue.blogApplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostTagServiceImpl implements PostTagService{

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    @Autowired
    public PostTagServiceImpl(PostRepository postRepository, TagRepository tagRepository, PostTagRepository postTagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    public void addTagToPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        PostTag postTag = new PostTag();
        postTag.setPost(post);
        postTag.setTag(tag);
        postTag.setCreatedAt(LocalDateTime.now());
        postTag.setUpdatedAt(LocalDateTime.now());

        postTagRepository.save(postTag);
    }

    public Set<Tag> getTagsByPostId(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        Set<Tag> tags = new HashSet<>();

        for (PostTag postTag : postTags) {
            tags.add(postTag.getTag());
        }

        return tags;
    }

    public void removeAllTagsFromPost(Long postId) {
        List<PostTag> tags = postTagRepository.findByPostId(postId);
        postTagRepository.deleteAll(tags);
    }
}
