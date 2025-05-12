package com.example.GuitarApp.services.authorization.impl;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.CommentRepository;
import com.example.GuitarApp.repositories.SongTutorialRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.authorization.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("authzComment")
public class CommentAuthorizationService implements AuthorizationService {

    private final CommentRepository commentRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CommentAuthorizationService(CommentRepository commentRepository, UserDetailsServiceImpl userDetailsService) {
        this.commentRepository = commentRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean canDelete(int commentId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();
        if (userDetails.isAdmin()) return true;

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || comment.getAuthor() == null) return false;

        return comment.getAuthor().getId() == userDetails.getId();
    }

    @Override
    public boolean canUpdate(int commentId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || comment.getAuthor() == null) return false;

        return comment.getAuthor().getId() == userDetails.getId();
    }
}
