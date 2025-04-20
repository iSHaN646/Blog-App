package io.mountblue.blogApplication.service;

import io.mountblue.blogApplication.entity.Comment;

import java.util.List;

public interface CommentService {

    void createComment(Comment comment);

    void deleteComment(Long id);

    void updateComment(Long id,String comment);

    Comment getComment(Long id);

}
