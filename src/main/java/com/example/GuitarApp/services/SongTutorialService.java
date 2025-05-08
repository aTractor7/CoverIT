package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.SongBeatRepository;
import com.example.GuitarApp.repositories.SongTutorialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongTutorialService implements CrudService<SongTutorial>{

    private final SongTutorialRepository songTutorialRepository;
    private final ErrorMessageService errMsg;
    private final UserDetailsServiceImpl userDetailsService;
    private final SongBeatRepository songBeatRepository;

    @Autowired
    public SongTutorialService(SongTutorialRepository songTutorialRepository, ErrorMessageService errMsg, UserDetailsServiceImpl userDetailsService, SongBeatRepository songBeatRepository) {
        this.songTutorialRepository = songTutorialRepository;
        this.errMsg = errMsg;
        this.userDetailsService = userDetailsService;
        this.songBeatRepository = songBeatRepository;
    }


    @Override
    public List<SongTutorial> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return songTutorialRepository.findAll(pageable).getContent();
    }

    public List<SongTutorial> findPage(int page, int pageSize, Optional<String> sortField, Optional<String> songTitle) {
        if (songTitle.isEmpty())
            return findPage(page, pageSize, sortField);

        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return songTutorialRepository.findBySongTitleContainingIgnoreCase(songTitle.get(), pageable).getContent();
    }

    @Override
    public SongTutorial findOne(int id) {
        return songTutorialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("songTutorial.notfound.byId", id)));
    }

    @Override
    @Transactional
    public SongTutorial create(SongTutorial songTutorial) {
        songTutorial.setTutorialAuthor(
                new User(userDetailsService.getCurrentUserDetails().getId()));

        return songTutorialRepository.save(songTutorial);
    }

    @Override
    @Transactional
    public SongTutorial update(int id, SongTutorial updatedTutorial) {
        SongTutorial tutorial = findOne(id);

        tutorial.setDifficulty(updatedTutorial.getDifficulty());
        tutorial.setDescription(updatedTutorial.getDescription());
        tutorial.setRecommendedStrumming(updatedTutorial.getRecommendedStrumming());

        //TODO: ця реалізація не дуже треба в ідеалі переробить і апдейтить по полям.
        if(tutorial.getSongBeats() != null)
            tutorial.getSongBeats().clear();

        for (SongBeat updatedBeat : updatedTutorial.getSongBeats()) {
            updatedBeat.setSongTutorial(tutorial);
            songBeatRepository.save(updatedBeat);
        }

        return tutorial;
    }

    @Override
    @Transactional
    public void delete(int id) {
        songTutorialRepository.deleteById(id);
    }
}
