package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Chord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChordRepository extends JpaRepository<Chord, Integer> {

    Optional<Chord> findByName(String name);

    Set<Chord> findByIdIn(Set<Integer> names);

    Set<Chord> findByNameIn(Set<String> names);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, int id);
}
