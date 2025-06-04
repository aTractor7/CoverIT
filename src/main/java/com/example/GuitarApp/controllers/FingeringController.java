package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.entity.dto.ChordShortDto;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.entity.dto.FingeringDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.FileStorageService;
import com.example.GuitarApp.services.FingeringsService;
import com.example.GuitarApp.util.validators.ExistsChordValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/fingerings")
public class FingeringController {

    private final FingeringsService fingeringsService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;
    private final ExistsChordValidator existsChordValidator;
    private final FileStorageService fileStorageService;

    @Autowired
    public FingeringController(FingeringsService fingeringsService,
                               ModelMapper modelMapper,
                               ErrorMessageService errMsg,
                               ExistsChordValidator existsChordValidator,
                               FileStorageService fileStorageService) {
        this.fingeringsService = fingeringsService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
        this.existsChordValidator = existsChordValidator;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<List<FingeringDto>> getAllPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField) {

        List<FingeringDto> fingerings = fingeringsService.findPage(page, size, sortField)
                .stream()
                .map(this::convertToFingeringDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(fingerings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FingeringDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToFingeringDto(fingeringsService.findOne(id)));
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws MalformedURLException {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    //TODO: add file extension check
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FingeringDto> save(
            @RequestPart("fingering") String fingeringDtoJson,
            @RequestPart(value = "image") MultipartFile imageFile,
            BindingResult bindingResult) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        FingeringDto fingeringDto = objectMapper.readValue(fingeringDtoJson, FingeringDto.class);

        existsChordValidator.validate(fingeringDto, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(imageFile);
            fingeringDto.setImgPath("/fingerings/images/" + storedFileName);
        }

        Fingering fingering = convertToFingering(fingeringDto);
        Fingering saved = fingeringsService.create(fingering);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToFingeringDto(saved));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FingeringDto> update(
            @PathVariable int id,
            @RequestPart("fingering") @Valid FingeringDto fingeringDto,
            @RequestPart(value = "image") MultipartFile imageFile,
            BindingResult bindingResult) throws IOException {

        existsChordValidator.validate(fingeringDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Fingering existingFingering = fingeringsService.findOne(id);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingFingering.getImgPath() != null) {
                fileStorageService.deleteFile(existingFingering.getImgPath());
            }

            String storedFileName = fileStorageService.storeFile(imageFile);
            fingeringDto.setImgPath("/fingerings/images/" + storedFileName);
        } else {
            fingeringDto.setImgPath(existingFingering.getImgPath());
        }

        Fingering updatedFingering = fingeringsService.update(id, convertToFingering(fingeringDto));
        return ResponseEntity.ok(convertToFingeringDto(updatedFingering));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) throws IOException {
        Fingering fingering = fingeringsService.findOne(id);

        if (fingering.getImgPath() != null) {
            fileStorageService.deleteFile(fingering.getImgPath());
        }

        fingeringsService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("fingering.deleted")));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Fingering validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<ErrorResponse> handleIOException(MalformedURLException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Wrong url", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "File operation failed", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private FingeringDto convertToFingeringDto(Fingering fingering) {
        return modelMapper.map(fingering, FingeringDto.class);
    }

    private Fingering convertToFingering(FingeringDto fingeringDto) {
        return modelMapper.map(fingeringDto, Fingering.class);
    }
}