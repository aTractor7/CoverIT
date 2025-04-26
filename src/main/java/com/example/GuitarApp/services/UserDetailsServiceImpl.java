package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ErrorMessageService errMsg;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, ErrorMessageService errMsg) {
        this.userRepository = userRepository;
        this.errMsg = errMsg;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        errMsg.getErrorMessage("user.notfound")
                ));
        return new UserDetailsImpl(user);
    }

    public UserDetailsImpl getCurrentUserDetails() {
        Authentication authentication = getAuthentication();

        return (UserDetailsImpl) authentication.getPrincipal();
    }

    public void changeUsernameInSecurityContext(String newUsername) {
        Authentication authentication = getAuthentication();
        UserDetails updatedDetails = loadUserByUsername(newUsername);


        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        updatedDetails,
                        authentication.getCredentials(),
                        updatedDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkAuthentication(authentication);
        return authentication;
    }

    private void checkAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return;
        }
        throw new AuthenticationCredentialsNotFoundException(errMsg.getErrorMessage("auth.invalid"));
    }
}
