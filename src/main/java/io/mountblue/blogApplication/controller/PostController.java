package io.mountblue.blogApplication.controller;

import io.mountblue.blogApplication.entity.Comment;
import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.entity.Tag;
import io.mountblue.blogApplication.repository.TagRepository;
import io.mountblue.blogApplication.service.PostService;
import io.mountblue.blogApplication.service.PostTagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostTagService postTagService;
    private final TagRepository tagRepository;

    public PostController(PostService postService, PostTagService postTagService, TagRepository tagRepository) {
        this.postService = postService;
        this.postTagService = postTagService;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "postForm";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute Post post,
                          @RequestParam("newTags") String newTags){

        postService.createPost(post,newTags);
        return "redirect:/posts";
    }


    @PostMapping("/update")
    public String addPost(@RequestParam Long id, @ModelAttribute Post post,
                          @RequestParam("newTags") String newTags){
        postService.updatePost(id,post,newTags);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delPost(@PathVariable Long id){
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post =  postService.getPost(id);
        model.addAttribute("post", post);

        Set<Tag> allTags = postTagService.getTagsByPostId(id);
        StringBuilder sb = new StringBuilder();

        for (Tag tag : allTags) {
            sb.append(tag.getName()).append(",");
        }
        String tagString = sb.toString();
        model.addAttribute("allTags", tagString);
        return "postForm";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPost(id);
        Set<Tag> allTags = postTagService.getTagsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("commentObj", new Comment());
        model.addAttribute("allTags", allTags);
        return "postDetail";
    }

    @GetMapping
    public String listPosts(
            @RequestParam(defaultValue = "1") int start,               // 1-based index
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) List<Long> tagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model
    ) {
        int page = (start - 1) / limit; // convert to 0-based page index
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sortField));

        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        Page<Post> postPage;

        boolean hasAuthor = authorName != null && !authorName.isEmpty();
        boolean hasTags = tagId != null && !tagId.isEmpty();
        boolean hasFromTo = from != null || to != null;

        if (hasAuthor && hasTags && hasFromTo) {
            postPage = postService.getPostsByAuthorTagsAndDateRange(authorName, tagId, fromDateTime, toDateTime, pageable);
        } else if (hasAuthor && hasTags) {
            postPage = postService.getPaginatedPostsByAuthorAndTags(authorName, tagId, pageable);
        } else if (hasAuthor && hasFromTo) {
            postPage = postService.getPostsByAuthorAndDateRange(authorName, fromDateTime, toDateTime, pageable);
        } else if (hasTags && hasFromTo) {
            postPage = postService.getPostsByTagsAndDateRange(tagId, fromDateTime, toDateTime, pageable);
        } else if (hasAuthor) {
            postPage = postService.getPaginatedPostsByAuthor(authorName, pageable);
        } else if (hasTags) {
            postPage = postService.getPaginatedPostsByTags(tagId, pageable);
        } else if (hasFromTo) {
            postPage = postService.getPostsByDateRange(fromDateTime, toDateTime, pageable);
        } else {
            postPage = postService.getPaginatedPosts(pageable);
        }

        List<Tag> allTags = tagRepository.findAll();

        StringBuilder tagParamString = new StringBuilder();
        if (tagId != null) {
            for (Long id : tagId) {
                tagParamString.append("&tagId=").append(id);
            }
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("start", page * limit + 1);
        model.addAttribute("limit", limit);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("order", order);
        model.addAttribute("authorName", authorName);
        model.addAttribute("allAuthors", postService.getAllDistinctAuthors());
        model.addAttribute("allTags", allTags);
        model.addAttribute("tagIds", tagId);
        model.addAttribute("tagParamString", tagParamString.toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedFrom = (from != null) ? from.format(formatter) : null;
        String formattedTo = (to != null) ? to.format(formatter) : null;
        model.addAttribute("from", formattedFrom);
        model.addAttribute("to", formattedTo);

        return "posts";
    }





}
