package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.repositories.PersonalLibraryRepository;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.PersonalLibraryService;
import com.example.GuitarApp.util.validators.PersonalLibraryValidator;
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
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/personal-library")
public class PersonalLibraryController {

    private final PersonalLibraryService personalLibraryService;
    private final ErrorMessageService errMsg;
    private final ModelMapper modelMapper;
    private final PersonalLibraryValidator validator;

    @Autowired
    public PersonalLibraryController(PersonalLibraryService personalLibraryService, ErrorMessageService errMsg, ModelMapper modelMapper, PersonalLibraryValidator validator) {
        this.personalLibraryService = personalLibraryService;
        this.errMsg = errMsg;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<List<PersonalLibraryDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField,
            @RequestParam(required = false) Optional<Integer> userId) {

        List<PersonalLibraryDto> songTutorials = personalLibraryService.findPage(page, size, sortField, userId)
                .stream()
                .map(this::convertToPersonalLibraryDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(songTutorials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalLibraryDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToPersonalLibraryDto(personalLibraryService.findOne(id)));
    }

    @PostMapping()
    public ResponseEntity<PersonalLibraryCreateDto> save(@RequestBody @Valid PersonalLibraryCreateDto personalLibraryCreateDto,
                                                      BindingResult bindingResult) {
        validator.validate(personalLibraryCreateDto, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        PersonalLibrary library = new PersonalLibrary(new SongTutorial(personalLibraryCreateDto.getTutorialId()));
        PersonalLibrary saved = personalLibraryService.create(library);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(new PersonalLibraryCreateDto(saved.getSongTutorial().getId()));
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, String>> delete(@RequestParam(name = "tutorialId") int tutorialId) {
        personalLibraryService.deleteByTutorialId(tutorialId);
        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("personalLibrary.deleted")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Personal library validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private PersonalLibraryDto convertToPersonalLibraryDto(PersonalLibrary personalLibrary) {
        return modelMapper.map(personalLibrary, PersonalLibraryDto.class);
    }
}
