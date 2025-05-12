package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommentService implements CrudService<Comment>{

    private final CommentRepository commentRepository;
    private final ErrorMessageService errMsg;
    private final UserDetailsServiceImpl userDetailsService;

    public CommentService(CommentRepository commentRepository, ErrorMessageService errMsg, UserDetailsServiceImpl userDetailsService) {
        this.commentRepository = commentRepository;
        this.errMsg = errMsg;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public List<Comment> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return commentRepository.findAll(pageable).getContent();
    }

    public List<Comment> findPage(int page, int pageSize, Optional<String> sortField, Optional<Integer> tutorialId) {
        if (tutorialId.isEmpty())
            return findPage(page, pageSize, sortField);

        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        List<Comment> comments = commentRepository.findBySongTutorial(new SongTutorial(tutorialId.get()), pageable).getContent();

        return comments.stream().filter(c -> c.getAnswerOn() == null).toList();
    }

    @Override
    public Comment findOne(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("comment.notfound.byId", id)
                ));
    }

    @Override
    @Transactional
    public Comment create(Comment comment) {
        comment.setAuthor(
                new User(userDetailsService.getCurrentUserDetails().getId()));

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(int id, Comment updatedComment) {
        Comment comment = findOne(id);

        comment.setText(updatedComment.getText());

        return comment;
    }

    @Override
    @Transactional
    public void delete(int id) {
        commentRepository.deleteById(id);
    }
}
