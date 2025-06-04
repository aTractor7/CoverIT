package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Chord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChordRepository extends JpaRepository<Chord, Integer> {

    Page<Chord> findAllByNameStartingWithAndNameNotContainingAndNameNotContaining(
            String nameStart, String exclude1, String exclude2, Pageable pageable);
    Page<Chord> findAllByNameStartingWith(String nameStart, Pageable pageable);


    Optional<Chord> findByName(String name);

    Set<Chord> findByIdIn(Set<Integer> names);

    Set<Chord> findByNameIn(Set<String> names);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, int id);


}
