package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.services.ArtistService;
import com.example.GuitarApp.services.ErrorMessageService;
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
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;

    public ArtistController(ArtistService artistService, ModelMapper modelMapper, ErrorMessageService errMsg) {
        this.artistService = artistService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
    }

    @GetMapping
    public ResponseEntity<List<ArtistDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField) {

        List<ArtistDto> artists = artistService.findPage(page, size, sortField)
                .stream()
                .map(this::convertToArtistDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToArtistDto(artistService.findOne(id)));
    }

    @PostMapping()
    public ResponseEntity<CreateArtistDto> save(@RequestBody @Valid CreateArtistDto artistDto,
                                                BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Artist artist = convertToArtist(artistDto);
        Artist saved = artistService.create(artist);


        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToCreateArtistDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authzArtist.canUpdate(#id)")
    public ResponseEntity<ArtistDto> update(@PathVariable int id,
                                                  @RequestBody @Valid CreateArtistDto artistDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Artist updatedArtist = artistService.update(id, convertToArtist(artistDto));
        return ResponseEntity.ok(convertToArtistDto(updatedArtist));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("@authzArtist.canDelete(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        artistService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("artist.deleted")));
    }

    private Artist convertToArtist(CreateArtistDto artistDto) {
        return modelMapper.map(artistDto, Artist.class);
    }

    private CreateArtistDto convertToCreateArtistDto(Artist artist) {
        return modelMapper.map(artist, CreateArtistDto.class);
    }

    private ArtistDto convertToArtistDto(Artist artist) {
        ArtistDto artistDto = modelMapper.map(artist, ArtistDto.class);

        if(artist.getCreatedBy() != null)
            artistDto.setCreator_id(artist.getCreatedBy().getId());

        return artistDto;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Artist validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
