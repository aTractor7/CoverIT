package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Chord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChordRepository extends JpaRepository<Chord, Integer> {
}
