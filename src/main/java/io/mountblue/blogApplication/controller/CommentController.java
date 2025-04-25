package io.mountblue.blogApplication.controller;

import io.mountblue.blogApplication.entity.Comment;
import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.entity.User;
import io.mountblue.blogApplication.repository.UserRepository;
import io.mountblue.blogApplication.service.CommentService;
import io.mountblue.blogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/posts/{postId}")
public class CommentController {

    private PostService postService;
    private CommentService commentService;
    private UserRepository userRepository;

    @Autowired
    public CommentController(PostService postService, CommentService commentService,UserRepository userRepository) {
        this.postService = postService;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/comments")
    public String addComment(@PathVariable Long postId,
                             @ModelAttribute("commentObj") Comment comment){
        Post post = postService.getPost(postId);
        comment.setPost(post);
        commentService.createComment(comment);
       // post.addComments(comment);
        return "redirect:/post" + postId;
    }

    @GetMapping("/comments/{commentId}/delete")
    public String delComment(@PathVariable Long postId,@PathVariable Long commentId,
                             Principal principal){
        Comment comment = commentService.getComment(commentId);
        Post post = postService.getPost(postId);
        if (principal != null) {
            String name = principal.getName(); // usually this is the username/email
            User user = userRepository.findByUserName(name);
            if(user.getEmail().equals(comment.getEmail()) ||
                    (user.getRole().equals("ADMIN"))
            ){
                commentService.deleteComment(commentId);
            }
        }
        return "redirect:/post" + postId;
    }

    @PostMapping("/comments/{commentId}/update")
    public String updateComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                @RequestParam("updatedComment") String commentText,
                                Principal principal){
        Comment comment = commentService.getComment(commentId);
        Post post = postService.getPost(postId);
        if (principal != null) {
            String name = principal.getName();
            User user = userRepository.findByUserName(name);
            if(user.getEmail().equals(comment.getEmail()) ||
                    (user.getRole().equals("ADMIN"))
            ){
             commentService.updateComment(commentId,commentText);
            }
        }
        return "redirect:/post" + postId;
    }
}
