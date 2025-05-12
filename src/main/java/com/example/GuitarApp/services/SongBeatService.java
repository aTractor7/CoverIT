package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.BeatChord;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.repositories.BeatChordRepository;
import com.example.GuitarApp.repositories.SongBeatRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SongBeatService implements CrudService<SongBeat> {

    private final SongBeatRepository songBeatRepository;
    private final BeatChordRepository beatChordRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public SongBeatService(SongBeatRepository songBeatRepository, BeatChordRepository beatChordRepository, ErrorMessageService errMsg) {
        this.songBeatRepository = songBeatRepository;
        this.beatChordRepository = beatChordRepository;
        this.errMsg = errMsg;
    }

    @Override
    public List<SongBeat> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return songBeatRepository.findAll(pageable).getContent();
    }

    @Override
    public SongBeat findOne(int id) {
        return songBeatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("songBeat.notfound.byId", id)
                ));
    }

    @Override
    @Transactional
    public SongBeat create(SongBeat songBeat) {
        return songBeatRepository.save(songBeat);
    }

    @Override
    @Transactional
    public SongBeat update(int id, SongBeat updatedSongBeat) {
        SongBeat songBeat = findOne(id);

        songBeat.setSongTutorial(updatedSongBeat.getSongTutorial());
        songBeat.setBeat(updatedSongBeat.getBeat());
        songBeat.setText(updatedSongBeat.getText());
        songBeat.setComment(updatedSongBeat.getComment());
        songBeat.setBeatChords(updatedSongBeat.getBeatChords());


        if(songBeat.getBeatChords() != null)
            songBeat.getBeatChords().clear();
        for(BeatChord updatedBeatChord: updatedSongBeat.getBeatChords()) {
            updatedBeatChord.setSongBeat(songBeat);
            beatChordRepository.save(updatedBeatChord);
        }

        return songBeat;
    }

    @Override
    @Transactional
    public void delete(int id) {
        songBeatRepository.deleteById(id);
    }
}
