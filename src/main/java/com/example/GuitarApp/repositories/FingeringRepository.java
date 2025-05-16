package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.Fingering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FingeringRepository extends JpaRepository<Fingering, Integer> {

    List<Fingering> findAllByChord(Chord chord);

    Set<Fingering> findByIdIn(Set<Integer> ids);

    Set<Fingering> findByImgPathIn(Set<String> imgPaths);
}
