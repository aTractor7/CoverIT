package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.dto.ArtistShortDto;
import com.example.GuitarApp.entity.dto.SongDto;
import com.example.GuitarApp.repositories.ArtistRepository;
import com.example.GuitarApp.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UniqueSongTitleAuthorValidator {

    //TODO: rewrite in future

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public UniqueSongTitleAuthorValidator(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    public boolean validate(SongDto songDto, Errors errors) {
        if (songDto == null || songDto.getSongAuthors() == null
                || songDto.getSongAuthors().isEmpty() || songDto.getTitle() == null)
            return true;
//        //////////////////////////////////////////////////////////////////////////////////

        List<ArtistShortDto> authors = new ArrayList<>(songDto.getSongAuthors());

        List<String> authorsName = authors.stream()
                .map(ArtistShortDto::getName)
                .toList();

        List<Artist> found = artistRepository.findByNameIn(authorsName);

        if (found.size() != authors.size()) {
            errors.rejectValue("songAuthors", "400", "No song authors with such name found");
        } else {
            // Перевіряємо, чи всі автори з однаковими іменами мають однакові ID
            for (ArtistShortDto dtoAuthor : authors) {
                Artist dbAuthor = found.stream()
                        .filter(a -> a.getName().equals(dtoAuthor.getName()))
                        .findFirst()
                        .orElse(null);

                if (dbAuthor == null || !(dbAuthor.getId() == dtoAuthor.getId())) {
                    errors.rejectValue("songAuthors", "400", "Mismatch between author names and IDs");
                    break;
                }
            }
        }
//        ///////////////////////////////////////////////////////////////////////////////


        Optional<Song> songOptional = songRepository.findByTitle(songDto.getTitle());

        if (songOptional.isEmpty() || songDto.getId() == songOptional.get().getId()) return true;

        Song song = songOptional.get();

        Set<Integer> songAuthorsIds = song.getSongAuthors().stream()
                .map(Artist::getId)
                .collect(Collectors.toSet());
        Set<Integer> songDtoAuthorsIds = songDto.getSongAuthors().stream()
                .map(ArtistShortDto::getId)
                .collect(Collectors.toSet());

        if(!songAuthorsIds.containsAll(songDtoAuthorsIds)) return true;

        errors.rejectValue("title", "400", "Song with this title and authors is already exists");
        return false;
    }
}
