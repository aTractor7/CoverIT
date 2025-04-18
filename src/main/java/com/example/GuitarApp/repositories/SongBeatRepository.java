package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.SongBeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongBeatRepository extends JpaRepository<SongBeat, Integer> {
}
