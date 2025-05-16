package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

    Page<Song> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Song> findByTitle(String title);

}
