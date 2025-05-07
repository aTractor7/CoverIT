package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.SongTutorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findBySongTutorial(SongTutorial songTutorial, Pageable pageable);
}
