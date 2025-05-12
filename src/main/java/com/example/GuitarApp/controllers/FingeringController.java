package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.entity.dto.FingeringDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.FingeringsService;
import com.example.GuitarApp.util.validators.ExistsChordValidator;
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
@RequestMapping("/fingerings")
public class FingeringController {

    private final FingeringsService fingeringsService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;
    private final ExistsChordValidator existsChordValidator;

    @Autowired
    public FingeringController(FingeringsService fingeringsService, ModelMapper modelMapper, ErrorMessageService errMsg, ExistsChordValidator existsChordValidator) {
        this.fingeringsService = fingeringsService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
        this.existsChordValidator = existsChordValidator;
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

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FingeringDto> save(@RequestBody @Valid FingeringDto fingeringDto,
                                        BindingResult bindingResult) {
        existsChordValidator.validate(fingeringDto, bindingResult);
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Fingering fingering = convertToFingering(fingeringDto);
        Fingering saved = fingeringsService.create(fingering);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(convertToFingeringDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FingeringDto> update(@PathVariable int id,
                                          @RequestBody @Valid FingeringDto fingeringDto,
                                          BindingResult bindingResult) {
        existsChordValidator.validate(fingeringDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        Fingering updatedFingering = fingeringsService.update(id, convertToFingering(fingeringDto));
        return ResponseEntity.ok(convertToFingeringDto(updatedFingering));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        fingeringsService.delete(id);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("fingering.deleted")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Fingering validation exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private FingeringDto convertToFingeringDto(Fingering fingering) {
        return modelMapper.map(fingering, FingeringDto.class);
    }

    private Fingering convertToFingering(FingeringDto fingeringDto) {
        return modelMapper.map(fingeringDto, Fingering.class);
    }
}
