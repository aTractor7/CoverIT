package com.example.GuitarApp.services.authorization.impl;

import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.SongRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.authorization.AuthorizationService;
import org.springframework.stereotype.Component;

@Component("authzSong")
public class SongAuthorizationService implements AuthorizationService {

    private final SongRepository songRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public SongAuthorizationService(SongRepository songRepository, UserDetailsServiceImpl userDetailsService) {
        this.songRepository = songRepository;
        this.userDetailsService = userDetailsService;
    }

    //TODO: можливо винести в абстракт клас бо тільки на 1 строку відрізняється
    @Override
    public boolean canDelete(int songId) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song == null) return false;

        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();
        if (userDetails.isAdmin()) return true;

        if(song.getCreatedBy() == null) return false;

        return song.getCreatedBy().getId() == userDetails.getId();
    }

    @Override
    public boolean canUpdate(int songId) {
        Song song = songRepository.findById(songId).orElse(null);
        if (song == null || song.getCreatedBy() == null) return false;

        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        return song.getCreatedBy().getId() == (userDetails.getId());
    }
}
