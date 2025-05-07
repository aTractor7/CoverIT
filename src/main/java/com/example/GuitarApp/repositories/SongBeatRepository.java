package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.entity.SongTutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface SongBeatRepository extends JpaRepository<SongBeat, Integer> {

    Set<SongBeat> findByIdIn(Set<Integer> ids);

    void deleteAllBySongTutorial(SongTutorial songTutorial);
}
