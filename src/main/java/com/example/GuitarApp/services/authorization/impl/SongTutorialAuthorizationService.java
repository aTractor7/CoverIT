package com.example.GuitarApp.services.authorization.impl;

import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.SongTutorialRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.authorization.AuthorizationService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component("authzSongTutorial")
public class SongTutorialAuthorizationService implements AuthorizationService {

    private final SongTutorialRepository songTutorialRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public SongTutorialAuthorizationService(SongTutorialRepository songTutorialRepository, UserDetailsServiceImpl userDetailsService) {
        this.songTutorialRepository = songTutorialRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean canDelete(int tutorialId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();
        if (userDetails.isAdmin()) return true;

        SongTutorial songTutorial = songTutorialRepository.findById(tutorialId).orElse(null);
        if (songTutorial == null || songTutorial.getTutorialAuthor() == null) return false;

        return songTutorial.getTutorialAuthor().getId() == userDetails.getId();
    }

    @Override
    public boolean canUpdate(int tutorialId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        SongTutorial songTutorial = songTutorialRepository.findById(tutorialId).orElse(null);
        if (songTutorial == null || songTutorial.getTutorialAuthor() == null) return false;

        return songTutorial.getTutorialAuthor().getId() == (userDetails.getId());
    }
}
