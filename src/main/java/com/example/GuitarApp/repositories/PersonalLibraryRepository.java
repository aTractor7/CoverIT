package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalLibraryRepository extends JpaRepository<PersonalLibrary, Integer> {

    Page<PersonalLibrary> findAllByOwner(User user, Pageable pageable);

    void deleteBySongTutorialAndOwner(SongTutorial songTutorial, User owner);

    boolean existsBySongTutorialAndOwner(SongTutorial songTutorial, User owner);
}
