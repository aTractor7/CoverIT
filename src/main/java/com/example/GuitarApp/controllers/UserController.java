package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.ChangePasswordDto;
import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.exceptions.PasswordChangeValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.GuitarApp.util.ErrorUtils.generateFieldErrorMessage;
import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ErrorMessageService errMsg;

    public UserController(UserService userService, ModelMapper modelMapper, ErrorMessageService errMsg) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.errMsg = errMsg;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersPageable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<String> sortField) {

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

    @GetMapping("/authenticated")
    public ResponseEntity<UserDto> getAuthenticatedUser() {
        return ResponseEntity.ok(convertToUserDto(userService.findAuthenticated()));
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

    @PutMapping("/{id}/update-password")
    @PreAuthorize("@authz.canUpdateUser(#id)")
    public ResponseEntity<Map<String, String>> updatePassword(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              @PathVariable int id,
                                                              @RequestBody @Valid ChangePasswordDto changePasswordDto,
                                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new PasswordChangeValidationException(generateFieldErrorMessage(bindingResult.getFieldErrors()));
        }

        userService.updatePassword(id, changePasswordDto.getNewPassword());
        userService.performLogout(request, response);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("user.password_changed")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authz.canDeleteUser(#id)")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int id,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        userService.delete(id);
        userService.performLogout(request, response);

        return ResponseEntity.ok(Map.of("message", errMsg.getErrorMessage("user.deleted")));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "User update exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PasswordChangeValidationException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Password update exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto convertToUserDto(User users) {
        return modelMapper.map(users, UserDto.class);
    }
}
