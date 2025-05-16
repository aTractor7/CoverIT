package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.dto.ChordDto;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.services.ChordService;
import com.example.GuitarApp.services.ErrorMessageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/chords")
public class ChordController {

    private final ChordService chordService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;

    @Autowired
    public ChordController(ChordService chordService, ModelMapper modelMapper, ErrorMessageService errMsg) {
        this.chordService = chordService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
    }

    @GetMapping
    public ResponseEntity<List<ChordDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField,
            @RequestParam(required = false) Optional<String> name) {

        List<ChordDto> chords = chordService.findPage(page, size, sortField, name)
                .stream()
                .map(this::convertToChordDto)
                .toList();

        return ResponseEntity.ok(chords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChordDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToChordDto(chordService.findOne(id)));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChordDto> save(@RequestBody @Valid ChordDto chordDto,
                                        BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Chord chord = convertToChord(chordDto);
        Chord saved = chordService.create(chord);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToChordDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChordDto> update(@PathVariable int id,
                                          @RequestBody @Valid ChordDto chordDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Chord updatedChord = chordService.update(id, convertToChord(chordDto));
        return ResponseEntity.ok(convertToChordDto(updatedChord));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        chordService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("chord.deleted")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Chord validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private ChordDto convertToChordDto(Chord chord) {

        return modelMapper.map(chord, ChordDto.class);
    }

    private Chord convertToChord(ChordDto chordDto) {
        return modelMapper.map(chordDto, Chord.class);
    }
}
