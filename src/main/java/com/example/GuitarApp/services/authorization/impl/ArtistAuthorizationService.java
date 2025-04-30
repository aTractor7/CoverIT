package com.example.GuitarApp.services.authorization.impl;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.ArtistRepository;
import com.example.GuitarApp.repositories.SongRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.authorization.AuthorizationService;
import org.springframework.stereotype.Component;

@Component("authzArtist")
public class ArtistAuthorizationService implements AuthorizationService {

    private final ArtistRepository artistRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public ArtistAuthorizationService(ArtistRepository artistRepository, UserDetailsServiceImpl userDetailsService) {
        this.artistRepository = artistRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean canDelete(int artistId) {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) return false;

        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();
        if (userDetails.isAdmin()) return true;

        if(artist.getCreatedBy() == null) return false;

        return artist.getCreatedBy().getId() == (userDetails.getId());
    }

    @Override
    public boolean canUpdate(int artistId) {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null || artist.getCreatedBy() == null) return false;

        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        return artist.getCreatedBy().getId() == (userDetails.getId());
    }
}
