package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.repositories.ChordRepository;
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
public class ChordService implements CrudService<Chord> {

    private final ChordRepository chordRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public ChordService(ChordRepository chordRepository, ErrorMessageService errMsg) {
        this.chordRepository = chordRepository;
        this.errMsg = errMsg;
    }

    @Override
    public List<Chord> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return chordRepository.findAll(pageable).getContent();
    }

    @Override
    public Chord findOne(int id) {
        return chordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("chord.notfound.byId", id)
                ));
    }

    @Override
    @Transactional
    public Chord create(Chord chord) {
        return chordRepository.save(chord);
    }

    @Override
    @Transactional
    public Chord update(int id, Chord updatedEntity) {
        Chord chord = findOne(id);

        chord.setName(updatedEntity.getName());

        return chord;
    }

    @Override
    @Transactional
    public void delete(int id) {
        chordRepository.deleteById(id);
    }
}
