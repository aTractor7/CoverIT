package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Jam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamRepository extends JpaRepository<Jam, Integer> {
}
