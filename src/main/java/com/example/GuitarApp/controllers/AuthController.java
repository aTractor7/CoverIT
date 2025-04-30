package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.UserRegistrationDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.tomcat.util.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;

    public AuthController(UserService userService, ModelMapper modelMapper, ErrorMessageService errMsg) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
    }


    @PostMapping("/registration")
    public ResponseEntity<UserRegistrationDto> registration(@RequestBody @Valid UserRegistrationDto userDto,
                                                            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        User user = convertToUser(userDto);
        User saved = userService.create(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.performLogout(request, response);
        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("auth.logout")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Registration exception",e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserRegistrationDto userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
