package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.BeatChord;
import com.example.GuitarApp.repositories.BeatChordRepository;
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
@Transactional
public class BeatChordService implements CrudService<BeatChord> {

    private final BeatChordRepository beatChordRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public BeatChordService(BeatChordRepository beatChordRepository, ErrorMessageService errMsg) {
        this.beatChordRepository = beatChordRepository;
        this.errMsg = errMsg;
    }

    @Override
    public List<BeatChord> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return beatChordRepository.findAll(pageable).getContent();
    }

    @Override
    public BeatChord findOne(int id) {
        return beatChordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("beatChord.notfound.byId", id)
                ));
    }

    @Override
    public BeatChord create(BeatChord entity) {
        return beatChordRepository.save(entity);
    }

    @Override
    public BeatChord update(int id, BeatChord updatedEntity) {
        BeatChord beatChord = findOne(id);

        beatChord.setChord(updatedEntity.getChord());
        beatChord.setSongBeat(updatedEntity.getSongBeat());
        beatChord.setRecommendedFingering(updatedEntity.getRecommendedFingering());

        return beatChord;
    }

    @Override
    public void delete(int id) {
        beatChordRepository.deleteById(id);
    }
}
