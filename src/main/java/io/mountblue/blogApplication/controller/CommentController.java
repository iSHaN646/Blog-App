package io.mountblue.blogApplication.controller;

import io.mountblue.blogApplication.entity.Comment;
import io.mountblue.blogApplication.entity.Post;
import io.mountblue.blogApplication.service.CommentService;
import io.mountblue.blogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts/{postId}")
public class CommentController {

    private PostService postService;
    private CommentService commentService;

    @Autowired
    public CommentController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    public String addComment(@PathVariable Long postId,
                             @ModelAttribute("commentObj") Comment comment){
        Post post = postService.getPost(postId);
        comment.setPost(post);
        commentService.createComment(comment);
        post.addComments(comment);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/comments/{commentId}/delete")
    public String delComment(@PathVariable Long postId,@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{commentId}/update")
    public String updateComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                @RequestParam("updatedComment") String commentText){
        commentService.updateComment(commentId,commentText);
        return "redirect:/posts/" + postId;
    }
}
