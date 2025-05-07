package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId(1);
        testComment.setText("Nice tutorial!");
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnComment_WhenIdExists() {
            given(commentRepository.findById(1)).willReturn(Optional.of(testComment));

            Comment result = commentService.findOne(1);

            assertThat(result.getText()).isEqualTo("Nice tutorial!");
        }

        @Test
        void shouldThrow_WhenCommentNotFound() {
            int id = 99;
            given(commentRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("comment.notfound.byId", id))
                    .willReturn("Comment with id " + id + " not found");

            assertThatThrownBy(() -> commentService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Comment with id 99 not found");
        }

        @Test
        void shouldReturnPageOfComments() {
            Comment another = new Comment();
            another.setText("Another comment");
            Page<Comment> page = new PageImpl<>(Arrays.asList(testComment, another));
            Pageable pageable = PageRequest.of(0, 2, Sort.by("text"));

            given(commentRepository.findAll(pageable)).willReturn(page);

            List<Comment> result = commentService.findPage(0, 2, Optional.of("text"));

            assertThat(result).hasSize(2).extracting("text")
                    .containsExactly("Nice tutorial!", "Another comment");
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateComment_WithCurrentUser() {
            User currentUser = new User();
            currentUser.setId(10);

            UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
            when(userDetails.getId()).thenReturn(currentUser.getId());
            when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);

            given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> invocation.getArgument(0));

            Comment newComment = new Comment();
            newComment.setText("Created comment");

            Comment saved = commentService.create(newComment);

            assertThat(saved.getAuthor().getId()).isEqualTo(10);
            assertThat(saved.getText()).isEqualTo("Created comment");
        }

        @Test
        void shouldUpdateCommentText_WhenExists() {
            Comment updated = new Comment();
            updated.setText("Updated text");

            given(commentRepository.findById(1)).willReturn(Optional.of(testComment));

            Comment result = commentService.update(1, updated);

            assertThat(result.getText()).isEqualTo("Updated text");
        }

        @Test
        void shouldDeleteCommentById() {
            commentService.delete(1);

            then(commentRepository).should().deleteById(1);
        }
    }
}
