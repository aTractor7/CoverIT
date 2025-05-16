package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.repositories.FingeringRepository;
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
public class FingeringsService implements CrudService<Fingering> {

    private final FingeringRepository fingeringRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public FingeringsService(FingeringRepository fingeringRepository, ErrorMessageService errMsg) {
        this.fingeringRepository = fingeringRepository;
        this.errMsg = errMsg;
    }

    @Override
    public List<Fingering> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return fingeringRepository.findAll(pageable).getContent();
    }

    public List<Fingering> findAllForChord(Chord chord) {
        return fingeringRepository.findAllByChord(chord);
    }

    @Override
    public Fingering findOne(int id) {
        return fingeringRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("fingering.notfound.byId", id)
                ));
    }

    @Override
    @Transactional
    public Fingering create(Fingering entity) {
        return fingeringRepository.save(entity);
    }

    @Override
    @Transactional
    public Fingering update(int id, Fingering updatedEntity) {
        Fingering fingering = findOne(id);

        fingering.setImgPath(updatedEntity.getImgPath());
        fingering.setChord(updatedEntity.getChord());

        return fingeringRepository.save(fingering);
    }

    @Override
    public void delete(int id) {
        fingeringRepository.deleteById(id);
    }
}