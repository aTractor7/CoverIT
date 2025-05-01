package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.repositories.SongBeatRepository;
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
public class SongBeatService implements CrudService<SongBeat> {

    private final SongBeatRepository songBeatRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public SongBeatService(SongBeatRepository songBeatRepository, ErrorMessageService errMsg) {
        this.songBeatRepository = songBeatRepository;
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
    public SongBeat create(SongBeat songBeat) {
        return songBeatRepository.save(songBeat);
    }

    @Override
    public SongBeat update(int id, SongBeat updatedSongBeat) {
        SongBeat songBeat = findOne(id);

        songBeat.setBeatChords(updatedSongBeat.getBeatChords());
        songBeat.setBeatChords(updatedSongBeat.getBeatChords());
        songBeat.setText(updatedSongBeat.getText());

        return songBeat;
    }

    @Override
    public void delete(int id) {
        songBeatRepository.deleteById(id);
    }
}
