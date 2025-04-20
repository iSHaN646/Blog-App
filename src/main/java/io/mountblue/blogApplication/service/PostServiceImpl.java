package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.entity.Tag;
import io.mountblue.blogApplication.repository.PostRepository;
import io.mountblue.blogApplication.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagService postTagService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagRepository tagRepository, PostTagService postTagService) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postTagService = postTagService;
    }

    @Override
    public void createPost(Post post,String newTags) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        Long newTagId = 0L;
        if (newTags != null && !newTags.trim().isEmpty()) {
            String[] tagNames = newTags.split(",");
            for (String name : tagNames) {
                name = name.trim();
                if (!name.isEmpty()) {
                    String finalName = name;
                    Tag tag = tagRepository.findByName(name)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(finalName);
                                newTag.setCreatedAt(LocalDateTime.now());
                                newTag.setUpdatedAt(LocalDateTime.now());
                                return tagRepository.save(newTag);
                            });
                    newTagId = tag.getId();
                }
            }
        }
        postRepository.save(post);
        postTagService.addTagToPost(post.getId(), newTagId);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void updatePost(Long id, Post post,String newTags) {
        Optional<Post> existingPost = postRepository.findById(id);

        if(existingPost.isPresent()){
            post.setPublished(existingPost.get().getPublished());
            post.setPublishedAt(existingPost.get().getPublishedAt());
            post.setCreatedAt(existingPost.get().getCreatedAt());
        }else{
            throw new RuntimeException("post not found");
        }
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        postTagService.removeAllTagsFromPost(id);

        if (newTags != null && !newTags.trim().isEmpty()) {
            String[] tagNames = newTags.split(",");
            for (String name : tagNames) {
                name = name.trim();
                if (!name.isEmpty()) {
                    String finalName = name;
                    Tag tag = tagRepository.findByName(name)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(finalName);
                                newTag.setCreatedAt(LocalDateTime.now());
                                newTag.setUpdatedAt(LocalDateTime.now());
                                return tagRepository.save(newTag);
                            });

                    postTagService.addTagToPost(post.getId(), tag.getId());
                }
            }
        }
    }

    @Override
    public Post getPost(Long id) {
        Optional<Post> post = postRepository.findById(id);

        if(post.isPresent()){
            return post.get();
        }else{
            throw new RuntimeException("Post not found");
        }
    }


    public Page<Post> getPaginatedPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> getPaginatedPostsByAuthor(String authorName, Pageable pageable) {
        return postRepository.findByAuthor(authorName, pageable);
    }

    @Override
    public List<String> getAllDistinctAuthors() {
        return postRepository.findAllDistinctAuthors();
    }

    public Page<Post> getPaginatedPostsByAuthorAndTags(String authorName, List<Long> tagIds, Pageable pageable) {
        return postRepository.findByAuthorAndTagsIn(authorName, tagIds, pageable);
    }

    @Override
    public Page<Post> getPaginatedPostsByTags(List<Long> tagIds, Pageable pageable) {
        if (tagIds == null || tagIds.isEmpty()) {
            return postRepository.findAll(pageable);
        }
        return postRepository.findDistinctByTagsIn(tagIds, pageable);
    }

    @Override
    public Page<Post> getPostsByAuthorTagsAndDateRange(String author, List<Long> tagIds, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return postRepository.findByAuthorAndTagsAndPublishedAtBetween(author, tagIds, from, to, pageable);
    }

    @Override
    public Page<Post> getPostsByAuthorAndDateRange(String author, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return postRepository.findByAuthorAndPublishedAtBetween(author, from, to, pageable);
    }

    @Override
    public Page<Post> getPostsByTagsAndDateRange(List<Long> tagIds, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return postRepository.findByTagsAndPublishedAtBetween(tagIds, from, to, pageable);
    }

    @Override
    public Page<Post> getPostsByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return postRepository.findByPublishedAtBetween(from, to, pageable);
    }

}
