package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SongTutorialRepository songTutorialRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;

    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        Song song = TestDataFactory.getSongWithAuthor();
        User user1 = TestDataFactory.getUser("First");
        User user2 = TestDataFactory.getUser("Second");

        userRepository.save(user1);
        userRepository.save(user2);
        songRepository.save(song);

        SongTutorial songTutorial = TestDataFactory.getSongTutorial();

        songTutorial.setTutorialAuthor(user1);
        songTutorial.setSong(song);
        song.setTutorials(Set.of(songTutorial));
        user1.setTutorials(Set.of(songTutorial));

        songTutorialRepository.save(songTutorial);

        comments = TestDataFactory.getComments();
        comments.forEach(comment -> comment.setSongTutorial(songTutorial));
        comments.get(0).setAuthor(user1);
        comments.get(1).setAuthor(user2);
        comments.get(2).setAuthor(user1);

        commentRepository.saveAll(comments);
    }

    @Test
    @DisplayName("Should find all comments")
    void shouldFindAllComments() {
        List<Comment> all = commentRepository.findAll();
        assertThat(all).hasSize(comments.size());
    }

    @Test
    @DisplayName("Should find comment by id")
    void shouldFindCommentById() {
        Comment saved = comments.get(0);
        Optional<Comment> found = commentRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo(saved.getText());
    }

    @Test
    @DisplayName("Should save comment")
    void shouldSaveNewComment() {
        Comment newComment = new Comment();
        newComment.setText("New test comment");
        newComment.setAuthor(comments.get(0).getAuthor());
        newComment.setSongTutorial(comments.get(0).getSongTutorial());
        newComment.setAnswerOn(comments.get(1));

        Comment saved = commentRepository.save(newComment);

        assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved.getText()).isEqualTo("New test comment");
        assertThat(saved.getAnswerOn()).isEqualTo(comments.get(1));
    }

    @Test
    @DisplayName("Comment relationship check")
    void shouldMaintainCommentHierarchy() {
        List<Comment> saved = commentRepository.findAll();
        Comment reply = saved.get(1);
        Comment parent = saved.get(0);

        assertThat(reply.getAnswerOn()).isEqualTo(parent);
    }
}