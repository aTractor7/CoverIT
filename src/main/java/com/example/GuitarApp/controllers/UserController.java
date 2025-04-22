package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.entity.dto.ChangePasswordDto;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.exceptions.PasswordChangeValidationException;
import com.example.GuitarApp.util.validators.PasswordValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordValidator passwordValidator;

    public UserController(UserService userService, ModelMapper modelMapper, PasswordValidator passwordValidator) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.passwordValidator = passwordValidator;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Optional<String> sortField) {

        List<UserDto> users = userService.findPage(page, size, sortField)
                .stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getOne(@PathVariable int id) {
        return ResponseEntity.ok(convertToUserDto(userService.findOne(id)));
    }


    @PutMapping("/{id}")
    @PreAuthorize("@authz.canUpdateUser(#id)")
    public ResponseEntity<UserDto> update(@PathVariable int id,
                                          @RequestBody @Valid UserDto userDTO,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        User updatedUser = userService.update(id, convertToUser(userDTO));
        return ResponseEntity.ok(convertToUserDto(updatedUser));
    }


    //TODO: try User user in @AuthenticationPrincipal
    @PutMapping("/{id}/update-password")
    @PreAuthorize("@authz.canUpdateUser(#id)")
    public ResponseEntity<Map<String, String>> updatePassword(@PathVariable int id,
                                                              @RequestBody @Valid ChangePasswordDto changePasswordDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              BindingResult bindingResult) {

        passwordValidator.validate(changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword(),
                userDetails, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new PasswordChangeValidationException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.updatePassword(id, changePasswordDto.getNewPassword());
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authz.canDeleteUser(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "User update exception",e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PasswordChangeValidationException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Password update exception",e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto convertToUserDto(User users) {
        return modelMapper.map(users, UserDto.class);
    }
}
