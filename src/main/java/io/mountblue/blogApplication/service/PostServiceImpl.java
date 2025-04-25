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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public void createPost(Post post, String newTags) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        postRepository.save(post); // Save first to generate the ID

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
                    Long newTagId = tag.getId();
                    postTagService.addTagToPost(post.getId(), newTagId); // now post.getId() is valid
                }
            }
        }
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
    public List<String> getAllDistinctAuthors() {
        return postRepository.findAllDistinctAuthors();
    }




    public Page<Post> getFilteredPosts(
            List<String> authorNames,
            List<Long> tagIds,
            LocalDate from,
            LocalDate to,
            String search,
            Pageable pageable
    ) {
        // Check if all filters are null or empty → fall back to findAll
        boolean noFilters =
                (authorNames == null || authorNames.isEmpty()) &&
                        (tagIds == null || tagIds.isEmpty()) &&
                        from == null &&
                        to == null &&
                        (search == null || search.trim().isEmpty());

        if (noFilters) {
            return getPaginatedPosts(pageable);  // fallback to normal pagination
        }
        // Convert LocalDate to LocalDateTime
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        // Calculate tagCount if tagIds is provided, otherwise set to null
        int tagCount = (tagIds != null && !tagIds.isEmpty()) ? (int) tagIds.size() : 0;

        // Format search string if it's provided, otherwise set to null
        String formattedSearch = (search != null && !search.trim().isEmpty()) ? search.trim().toLowerCase() : null;

        // Filter authorNames and tagIds, set to empty list if empty, null if null
        List<String> filteredAuthorNames = (authorNames != null && !authorNames.isEmpty()) ? authorNames : null;
        List<Long> filteredTagIds = (tagIds != null && !tagIds.isEmpty()) ? tagIds : null;

        // Call repository method with only non-null parameters
        return postRepository.findFilteredPostsWithAllTags(
                filteredAuthorNames,
                filteredTagIds,
                tagCount,
                fromDateTime,
                toDateTime,
                formattedSearch,
                pageable
        );
    }






}
