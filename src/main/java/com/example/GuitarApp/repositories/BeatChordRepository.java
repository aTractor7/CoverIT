package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.BeatChord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeatChordRepository extends JpaRepository<BeatChord, Integer> {
}
