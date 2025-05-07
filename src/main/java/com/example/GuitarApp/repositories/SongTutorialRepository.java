package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SongTutorialRepository extends JpaRepository<SongTutorial, Integer> {

    @Query("SELECT st FROM SongTutorial st JOIN st.song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :songTitle, '%'))")
    Page<SongTutorial> findBySongTitleContainingIgnoreCase(String songTitle, Pageable pageable);

    Set<SongTutorial> findAllByTutorialAuthor(User tutorialAuthor);
}
