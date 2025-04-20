package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Comment;
import io.mountblue.blogApplication.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService{

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void updateComment(Long id, String commentText) {
        Comment comment = getComment(id);
        comment.setComment(commentText);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public Comment getComment(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if(comment.isPresent()){
            return comment.get();
        }else{
            throw new RuntimeException("comment not found");
        }
    }
}
