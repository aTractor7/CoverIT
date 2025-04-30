package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.ArtistRepository;
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
public class ArtistService implements CrudService<Artist> {

    private final ArtistRepository artistRepository;
    private final ErrorMessageService errMsg;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ErrorMessageService errMsg, UserDetailsServiceImpl userDetailsService) {
        this.artistRepository = artistRepository;
        this.errMsg = errMsg;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public List<Artist> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return artistRepository.findAll(pageable).getContent();
    }

    @Override
    public Artist findOne(int id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("artist.notfound.byId", id)
                ));
    }

    public Artist findOne(String name) {
        return artistRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("artist.notfound.byName", name)
                ));
    }

    @Override
    @Transactional
    public Artist create(Artist artist) {
        artist.setCreatedBy(
                new User(userDetailsService.getCurrentUserDetails().getId()));
        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public Artist update(int id, Artist updatedArtist) {
        Artist artist = findOne(id);

        artist.setName(updatedArtist.getName());
        artist.setBio(updatedArtist.getBio());

        return artist;
    }

    @Override
    @Transactional
    public void delete(int id) {
        artistRepository.deleteById(id);
    }
}
