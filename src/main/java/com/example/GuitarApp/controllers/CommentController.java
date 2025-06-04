package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.services.CommentService;
import com.example.GuitarApp.services.ErrorMessageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;

    @Autowired
    public CommentController(CommentService commentService, ModelMapper modelMapper, ErrorMessageService errMsg) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "comments") Optional<String> sortField,
            @RequestParam(required = false) Optional<Integer> tutorialId) {

        List<CommentDto> songTutorials = commentService.findPage(page, size, sortField, tutorialId)
                .stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(songTutorials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToCommentDto(commentService.findOne(id)));
    }

    @PostMapping()
    public ResponseEntity<CommentCreateDto> save(@RequestBody @Valid CommentCreateDto commentDto,
                                                      BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Comment comment = convertToComment(commentDto);
        Comment saved = commentService.create(comment);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToCommentCreateDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authzComment.canUpdate(#id)")
    public ResponseEntity<CommentDto> update(@PathVariable int id, @RequestBody @Valid CommentDto commentDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Comment updated = commentService.update(id, convertToComment(commentDto));
        return ResponseEntity.ok(convertToCommentDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authzComment.canDelete(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        commentService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("comment.deleted")));
    }

    private Comment convertToComment(CommentCreateDto commentCreateDto) {
        Comment comment = modelMapper.map(commentCreateDto, Comment.class);

        comment.setId(0);
        if (commentCreateDto.getIdAnswerOn() != 0) {
            comment.setAnswerOn(new Comment(commentCreateDto.getIdAnswerOn()));
        }

        return comment;
    }

    private CommentCreateDto convertToCommentCreateDto(Comment comment) {
        return modelMapper.map(comment, CommentCreateDto.class);
    }

    private Comment convertToComment(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }

    private CommentDto convertToCommentDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }
}
