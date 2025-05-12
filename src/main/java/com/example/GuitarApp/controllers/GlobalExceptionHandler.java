package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.dto.ErrorResponse;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.exceptions.UniqueFieldValidatorConfigurationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.GuitarApp.util.ErrorUtils.getStackTraceAsString;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final UserService userService;

    public GlobalExceptionHandler(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Entity not found exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e) {
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Username not found exception", e.getMessage(), getStackTraceAsString(e));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AuthenticationCredentialsNotFoundException e,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                "Authentication credentials not found error", e.getMessage(), getStackTraceAsString(e));

        userService.performLogout(request, response);

        return new ResponseEntity<>(errResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AuthorizationDeniedException e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                "No permit for such operation", e.getMessage(), getStackTraceAsString(e));

        return new ResponseEntity<>(errResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(UniqueFieldValidatorConfigurationException e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unique field validator configuration failed", e.getMessage(), getStackTraceAsString(e));

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ValidationException e) {
        if(e.getCause() instanceof UniqueFieldValidatorConfigurationException)
            handleException((UniqueFieldValidatorConfigurationException) e.getCause());

        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Validation Exception", e.getMessage(), getStackTraceAsString(e));

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(MissingServletRequestParameterException e) {
        if(e.getCause() instanceof UniqueFieldValidatorConfigurationException)
            handleException((UniqueFieldValidatorConfigurationException) e.getCause());

        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Request Param Exception", e.getMessage(), getStackTraceAsString(e));

        return new ResponseEntity<>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                getStackTraceAsString(ex)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
