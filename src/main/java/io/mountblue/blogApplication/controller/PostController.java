package io.mountblue.blogApplication.controller;

import io.mountblue.blogApplication.entity.Comment;
import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.entity.Tag;
import io.mountblue.blogApplication.entity.User;
import io.mountblue.blogApplication.repository.TagRepository;
import io.mountblue.blogApplication.repository.UserRepository;
import io.mountblue.blogApplication.service.PostService;
import io.mountblue.blogApplication.service.PostTagService;
import io.mountblue.blogApplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class PostController {

    private final PostService postService;
    private final PostTagService postTagService;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public PostController(PostService postService, PostTagService postTagService, TagRepository tagRepository, UserService userService, UserRepository userRepository) {
        this.postService = postService;
        this.postTagService = postTagService;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/newpost")
    public String showCreateForm(Model model,Principal principal) {
        model.addAttribute("post", new Post());

        if (principal != null) {
            String name = principal.getName(); // usually this is the username/email
            User user = userRepository.findByUserName(name);
            model.addAttribute("loggedInUser", user);
        }
        return "postForm";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute Post post,
                          @RequestParam("newTags") String newTags){

        postService.createPost(post,newTags);
        return "redirect:/";
    }


    @PostMapping("/update")
    public String addPost(@RequestParam Long id, @ModelAttribute Post post,
                          @RequestParam("newTags") String newTags){
        postService.updatePost(id,post,newTags);
        return "redirect:/post" + id;
    }

    @GetMapping("/delete/{id}")
    public String delPost(@PathVariable Long id,Principal principal){
        Post post =  postService.getPost(id);
        if (principal != null) {
            String name = principal.getName(); // usually this is the username/email
            User user = userRepository.findByUserName(name);
            if(user.getUserName().equals(post.getAuthor()) ||
                    (user.getRole().equals("ADMIN"))
            ){
                postService.deletePost(id);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,Principal principal) {
        Post post =  postService.getPost(id);

        if (principal != null) {
            String name = principal.getName();
            User user = userRepository.findByUserName(name);
            model.addAttribute("loggedInUser", user);
            if((!Objects.equals(user.getRole(), "ADMIN")) && (!Objects.equals(user.getUserName(), post.getAuthor()))){
                return "redirect:/";
            }
        }
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

    @GetMapping("/post{id}")
    public String viewPost(@PathVariable Long id, Model model,Principal principal) {
        Post post = postService.getPost(id);
        Set<Tag> allTags = postTagService.getTagsByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("commentObj", new Comment());
        model.addAttribute("allTags", allTags);

        if (principal != null) {
            String name = principal.getName(); // usually this is the username/email
            User user = userRepository.findByUserName(name);
            model.addAttribute("loggedInUser", user);
        }
        return "postDetail";
    }

    @GetMapping("/")
    public String listPosts(
            @RequestParam(defaultValue = "1") int start,
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false, name = "authorName") List<String> authorNames,
            @RequestParam(required = false) List<Long> tagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            Principal principal,
            Model model
    ) {

        int page = (start - 1) / limit;
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sortField));

        Page<Post> postPage = postService.getFilteredPosts(authorNames, tagId, from, to,search, pageable);


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
        model.addAttribute("authorNames", authorNames); // updated
        model.addAttribute("allAuthors", postService.getAllDistinctAuthors());
        model.addAttribute("allTags", allTags);
        model.addAttribute("tagIds", tagId);
        model.addAttribute("tagParamString", tagParamString.toString());
        model.addAttribute("search", search);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedFrom = (from != null) ? from.format(formatter) : null;
        String formattedTo = (to != null) ? to.format(formatter) : null;
        model.addAttribute("from", formattedFrom);
        model.addAttribute("to", formattedTo);

        if (principal != null) {
            String name = principal.getName(); // usually this is the username/email
            User user = userRepository.findByUserName(name);
            model.addAttribute("loggedInUser", user);
        }

        return "posts";
    }







}
