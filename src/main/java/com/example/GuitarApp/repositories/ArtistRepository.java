package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    Optional<Artist> findByName(String name);

    List<Artist> findByNameIn(List<String> names);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, int id);
}
