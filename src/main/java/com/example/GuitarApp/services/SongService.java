package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.ArtistRepository;
import com.example.GuitarApp.repositories.SongRepository;
import com.example.GuitarApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongService implements CrudService<Song>{
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final ErrorMessageService errMsg;
    private final UserDetailsServiceImpl userDetailsService;

    public SongService(SongRepository songRepository, ArtistRepository artistRepository, ErrorMessageService errMsg, UserDetailsServiceImpl userDetailsService) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.errMsg = errMsg;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public List<Song> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return songRepository.findAll(pageable).getContent();
    }

    public List<Song> findPage(int page, int pageSize, Optional<String> sortField, Optional<String> title) {
        if (title.isEmpty())
            return findPage(page, pageSize, sortField);
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return songRepository.findAllByTitleContainingIgnoreCase(title.get(), pageable).getContent();
    }

    @Override
    public Song findOne(int id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("song.notfound.byId", id)
                ));
    }

    public Song findOne(String title) {
        return songRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("song.notfound.byTitle", title)
                ));
    }

    @Override
    @Transactional
    public Song create(Song song) {
        song.setCreatedBy(
                new User(userDetailsService.getCurrentUserDetails().getId()));

        List<Artist> artists = artistRepository.findAllById(
                song.getSongAuthors().stream()
                        .map(Artist::getId)
                        .collect(Collectors.toSet()));

        song.setSongAuthors(new HashSet<>(artists));
        song.getSongAuthors().forEach(author -> author.getSongs().add(song));

        return songRepository.save(song);
    }

    @Override
    @Transactional
    public Song update(int id, Song updatedSong) {
        Song song = findOne(id);

        song.setTitle(updatedSong.getTitle());
        song.setGenre(updatedSong.getGenre());
        song.setReleaseDate(updatedSong.getReleaseDate());

        if(!updatedSong.getSongAuthors().equals(song.getSongAuthors())) {
            List<Artist> artists = artistRepository.findAllById(
                    updatedSong.getSongAuthors().stream()
                            .map(Artist::getId)
                            .collect(Collectors.toSet()));

            song.setSongAuthors(new HashSet<>(artists));

            song.getSongAuthors().forEach(author -> author.getSongs().add(song));
        }
        return song;
    }

    @Override
    @Transactional
    public void delete(int id) {
        songRepository.deleteById(id);
    }
}
