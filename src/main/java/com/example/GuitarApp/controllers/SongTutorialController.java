package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Comment;
import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.PersonalLibraryService;
import com.example.GuitarApp.services.SongTutorialService;
import com.example.GuitarApp.util.validators.SongTutorialValidator;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/tutorials")
public class SongTutorialController {

    private final SongTutorialService songTutorialService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;
    private final SongTutorialValidator songTutorialValidator;

    @Autowired
    public SongTutorialController(SongTutorialService songTutorialService, ModelMapper modelMapper, ErrorMessageService errMsg, SongTutorialValidator songTutorialValidator) {
        this.songTutorialService = songTutorialService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
        this.songTutorialValidator = songTutorialValidator;
    }

    @GetMapping
    public ResponseEntity<List<SongTutorialShortDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField,
            @RequestParam(required = false) Optional<String> songTitle) {

        List<SongTutorialShortDto> songTutorials = songTutorialService.findPage(page, size, sortField, songTitle)
                .stream()
                .map(this::convertToSongTutorialShortDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(songTutorials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongTutorialDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToSongTutorialDto(songTutorialService.findOne(id)));
    }

    @PostMapping()
    public ResponseEntity<SongTutorialCreateDto> save(@RequestBody @Valid SongTutorialCreateDto songTutorialCreateDto,
                                        BindingResult bindingResult) {
        songTutorialValidator.validate(songTutorialCreateDto, bindingResult, false);
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        SongTutorial tutorial = convertToSongTutorial(songTutorialCreateDto);
        SongTutorial saved = songTutorialService.create(tutorial);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToSongTutorialCreateDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authzSongTutorial.canUpdate(#id)")
    public ResponseEntity<SongTutorialDto> update(@PathVariable int id,
                                          @RequestBody @Valid SongTutorialDto songTutorialDto,
                                          BindingResult bindingResult) {
        songTutorialValidator.validate(
                modelMapper.map(songTutorialDto, SongTutorialCreateDto.class), bindingResult, true);
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        SongTutorial updated = songTutorialService.update(id, convertToSongTutorial(songTutorialDto));
        return ResponseEntity.ok(convertToSongTutorialDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authzSongTutorial.canDelete(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        songTutorialService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("songTutorial.deleted")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Song tutorial validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //TODO: винести всі мапери в окремий клас
    private SongTutorial convertToSongTutorial(SongTutorialCreateDto songTutorialCreateDto) {
        SongTutorial songTutorial = modelMapper.map(songTutorialCreateDto, SongTutorial.class);
        setupSongTutorialReferences(songTutorial);

        return songTutorial;
    }

    private SongTutorialCreateDto convertToSongTutorialCreateDto(SongTutorial songTutorial) {
        return modelMapper.map(songTutorial, SongTutorialCreateDto.class);
    }

    private SongTutorial convertToSongTutorial(SongTutorialDto songTutorialDto) {
        SongTutorial songTutorial = modelMapper.map(songTutorialDto, SongTutorial.class);
        setupSongTutorialReferences(songTutorial);

        return songTutorial;
    }

    private void setupSongTutorialReferences(SongTutorial songTutorial) {
        songTutorial.getSongBeats()
                .forEach(beat -> beat.setSongTutorial(songTutorial));

        for(SongBeat songBeat : songTutorial.getSongBeats()) {
            songBeat.getBeatChords()
                    .forEach(beatChord -> beatChord.setSongBeat(songBeat));
        }
    }

    //TODO: якщо в chord beat recommendedFingering.getChord.getId() співпадає з chord.getId() виникає помилка
    private SongTutorialDto convertToSongTutorialDto(SongTutorial songTutorial) {
        Set<Comment> commentSet = songTutorial.getComments();
        songTutorial.setComments(null);

        SongTutorialDto songTutorialDto = modelMapper.map(songTutorial, SongTutorialDto.class);

        if(commentSet != null)
            songTutorialDto.setComments(
                    commentSet.stream()
                            .map(c -> modelMapper.map(c, CommentDto.class))
                            .collect(Collectors.toSet())
            );
        return songTutorialDto;
    }

    private SongTutorialShortDto convertToSongTutorialShortDto(SongTutorial songTutorial) {
        return modelMapper.map(songTutorial, SongTutorialShortDto.class);
    }
}
