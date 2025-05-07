package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.PersonalLibraryCreateDto;
import com.example.GuitarApp.entity.dto.SongTutorialCreateDto;
import com.example.GuitarApp.repositories.PersonalLibraryRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PersonalLibraryValidator {

    private final PersonalLibraryRepository personalLibraryRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PersonalLibraryValidator(PersonalLibraryRepository personalLibraryRepository, UserDetailsServiceImpl userDetailsService) {
        this.personalLibraryRepository = personalLibraryRepository;
        this.userDetailsService = userDetailsService;
    }

    public boolean validate(PersonalLibraryCreateDto personalLibraryCreateDto, Errors errors){
        if(personalLibraryCreateDto == null || personalLibraryCreateDto.getTutorialId() == null)
            return true;

        User user = userDetailsService.getCurrentUserDetails().user();
        SongTutorial tutorial = new SongTutorial(personalLibraryCreateDto.getTutorialId());

        if(personalLibraryRepository.existsBySongTutorialAndOwner(tutorial, user)){
            errors.rejectValue("tutorialId", "400", "You already have this tutorial in library");
            return false;
        }

        return true;
    }
}
