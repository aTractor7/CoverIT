package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.UserRegistrationDto;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserService userService;
    private final ModelMapper modelMapper;

    public AuthController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }


    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> registration(@RequestBody @Valid UserRegistrationDto userDto,
                                                            BindingResult bindingResult) {
        User user = convertToUser(userDto);

        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.register(user);

        //TODO: розібратись з цим. Треба чекнуть чи у юзера після реєстрації з'являється id і додати хедер в респонс ентіті
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Location", "/api/users/" + userId);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Registration exception",e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserRegistrationDto userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
