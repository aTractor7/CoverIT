package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.UserDetailsImpl;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    private final UserDetailsServiceImpl userDetailsService;

    public AuthorizationService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public boolean canDeleteUser(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        if (userDetails.isAdmin()) return true;

        return userDetails.getId() == targetUserId;
    }

    public boolean canUpdateUser(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        return userDetails.getId() == targetUserId;
    }
}
