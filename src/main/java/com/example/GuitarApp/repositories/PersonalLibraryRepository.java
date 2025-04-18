package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.PersonalLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalLibraryRepository extends JpaRepository<PersonalLibrary, Integer> {
}
