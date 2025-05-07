package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.dto.ArtistShortDto;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.entity.dto.SongDto;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.SongService;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.validators.UniqueSongTitleAuthorValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;
    private final UniqueSongTitleAuthorValidator uniqueSongTitleAuthorValidator;

    public SongController(SongService songService, ModelMapper modelMapper, ErrorMessageService errMsg, UniqueSongTitleAuthorValidator uniqueSongTitleAuthorValidator) {
        this.songService = songService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
        this.uniqueSongTitleAuthorValidator = uniqueSongTitleAuthorValidator;
    }

    @GetMapping
    public ResponseEntity<List<SongDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField) {

        List<SongDto> songs = songService.findPage(page, size, sortField)
                .stream()
                .map(this::convertToSongDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToSongDto(songService.findOne(id)));
    }

    @PostMapping()
    public ResponseEntity<SongDto> save(@RequestBody @Valid SongDto songDto,
                                          BindingResult bindingResult) {
        uniqueSongTitleAuthorValidator.validate(songDto, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Song song = convertToSong(songDto);
        Song saved = songService.create(song);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToSongDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authzSong.canUpdate(#id)")
    public ResponseEntity<SongDto> update(@PathVariable int id,
                                            @RequestBody @Valid SongDto songDto,
                                            BindingResult bindingResult) {
        uniqueSongTitleAuthorValidator.validate(songDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Song updatedSong = songService.update(id, convertToSong(songDto));
        return ResponseEntity.ok(convertToSongDto(updatedSong));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("@authzSong.canDelete(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        songService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("song.deleted")));
    }

    private Song convertToSong(SongDto songDto) {
        return modelMapper.map(songDto, Song.class);
    }

    private SongDto convertToSongDto(Song song) {
        SongDto songDto = modelMapper.map(song, SongDto.class);

        if(song.getCreatedBy() != null)
            songDto.setCreator_id(song.getCreatedBy().getId());

        return songDto;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Song validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
