package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component("authz")
public class AuthorizationService {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthorizationService(UserService userService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    //TODO: подумати над тим щоб не звертатись до бд.
    public boolean canDeleteUser(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        if (userDetails.isAdmin()) return true;

        try {
            int currentUserId = userService.findOne(userDetails.getUsername()).getId();
            return currentUserId == targetUserId;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    public boolean canUpdateUser(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        try {
            int currentUserId = userService.findOne(userDetails.getUsername()).getId();
            return currentUserId == targetUserId;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
