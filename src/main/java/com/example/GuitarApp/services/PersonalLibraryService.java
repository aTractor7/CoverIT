package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.PersonalLibraryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonalLibraryService implements CrudService<PersonalLibrary>{

    private final PersonalLibraryRepository personalLibraryRepository;
    private final ErrorMessageService errMsg;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PersonalLibraryService(PersonalLibraryRepository personalLibraryRepository, ErrorMessageService errMsg, UserDetailsServiceImpl userDetailsService) {
        this.personalLibraryRepository = personalLibraryRepository;
        this.errMsg = errMsg;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public List<PersonalLibrary> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return personalLibraryRepository.findAll(pageable).getContent();
    }

    public List<PersonalLibrary> findPage(int page, int pageSize, Optional<String> sortField, Optional<Integer> userId) {
        if(userId.isEmpty()) return findPage(page, pageSize, sortField);

        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return personalLibraryRepository.findAllByOwner(new User(userId.get()), pageable).getContent();
    }

    @Override
    public PersonalLibrary findOne(int id) {
        return personalLibraryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("personalLibrary.notfound.byId", id)
                ));
    }

    @Override
    @Transactional
    public PersonalLibrary create(PersonalLibrary personalLibrary) {
        personalLibrary.setOwner(
                new User(userDetailsService.getCurrentUserDetails().getId()));

        return personalLibraryRepository.save(personalLibrary);
    }

    @Override
    @Transactional
    public PersonalLibrary update(int id, PersonalLibrary updatedPersonalLibrary) {
        PersonalLibrary personalLibrary = findOne(id);

        personalLibrary.setSongTutorial(updatedPersonalLibrary.getSongTutorial());

        return personalLibrary;
    }

    @Override
    @Transactional
    public void delete(int id) {
        personalLibraryRepository.deleteById(id);
    }

    @Transactional
    public void deleteByTutorialId(int tutorialId) {
        User owner = userDetailsService.getCurrentUserDetails().user();
        SongTutorial tutorial = new SongTutorial(tutorialId);

        personalLibraryRepository.deleteBySongTutorialAndOwner(tutorial, owner);
    }
}
