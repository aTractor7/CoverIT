package com.example.GuitarApp.services.authorization.impl;

import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.authorization.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("authzUser")
public class UserAuthorizationService implements AuthorizationService {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserAuthorizationService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean canDelete(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        if (userDetails.isAdmin()) return true;

        return userDetails.getId() == targetUserId;
    }

    @Override
    public boolean canUpdate(int targetUserId) {
        UserDetailsImpl userDetails = userDetailsService.getCurrentUserDetails();

        return userDetails.getId() == targetUserId;
    }
}
