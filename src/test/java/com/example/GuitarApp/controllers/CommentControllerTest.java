package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.CommentCreateDto;
import com.example.GuitarApp.entity.dto.CommentDto;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.services.CommentService;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.authorization.impl.CommentAuthorizationService;
import com.example.GuitarApp.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private CommentAuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment comment;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        User author = TestDataFactory.getUser();
        comment = TestDataFactory.getComments().get(0);
        comment.setAuthor(author);

        commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreatedAt(comment.getCreatedAt());
        UserDto userDto = new UserDto();
        userDto.setId(comment.getAuthor().getId());
        commentDto.setAuthor(userDto);

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Nice tutorial!");
        commentCreateDto.setSongTutorialId(0);
    }

    @Test
    void getAllPageable_shouldReturnListOfComments() throws Exception {
        when(commentService.findPage(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(comment));
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(get("/comments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(0));
    }

    @Test
    void getOne_shouldReturnSingleComment() throws Exception {
        when(commentService.findOne(1)).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @Test
    void save_shouldCreateComment() throws Exception {
        when(modelMapper.map(any(CommentCreateDto.class), eq(Comment.class))).thenReturn(comment);
        when(commentService.create(any(Comment.class))).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(CommentCreateDto.class))).thenReturn(commentCreateDto);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Nice tutorial!"));
    }

    @Test
    void update_shouldUpdateComment() throws Exception {
        when(authorizationService.canUpdate(1)).thenReturn(true);
        when(modelMapper.map(any(CommentDto.class), eq(Comment.class))).thenReturn(comment);
        when(commentService.update(eq(1), any(Comment.class))).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0));
    }

    @Test
    void delete_shouldDeleteComment() throws Exception {
        when(authorizationService.canDelete(1)).thenReturn(true);
        when(errorMessageService.getErrorMessage("comment.deleted")).thenReturn("Comment was deleted");

        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment was deleted"));
    }
}
