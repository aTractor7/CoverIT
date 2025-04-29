package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService implements CrudService<User>{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final LogoutHandler logoutHandler;
    private final ErrorMessageService errMsg;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService, LogoutHandler logoutHandler, ErrorMessageService errMsg) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.logoutHandler = logoutHandler;
        this.errMsg = errMsg;
    }

    public List<User> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return userRepository.findAll(pageable).getContent();
    }

    public User findOne(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("user.notfound.byId", id)
                ));
    }

    public User findOne(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("user.notfound.byUsername", username)
                ));
    }

    public User findAuthenticated() {
        UserDetails userDetails = userDetailsService.getCurrentUserDetails();
        return findOne(userDetails.getUsername());
    }

    public boolean matchPassword(String checked, String encodedPassword) {
        return passwordEncoder.matches(checked, encodedPassword);
    }

    @Transactional
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setJoinDate(LocalDate.now());
        return userRepository.save(user);
    }

    @Transactional
    public User update(int id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("user.notfound.byId", id)
                ));

        boolean usernameChanged = !user.getUsername().equals(updatedUser.getUsername());

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setSkill(updatedUser.getSkill());
        user.setInstrument(updatedUser.getInstrument());
        user.setBio(updatedUser.getBio());
        user.setProfileImg(updatedUser.getProfileImg());

        if(usernameChanged) {
            userDetailsService.changeUsernameInSecurityContext(updatedUser.getUsername());
        }

        return user;
    }

    @Transactional
    public void updatePassword(int id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        errMsg.getErrorMessage("user.notfound.byId", id)
                ));

        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    public void performLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(request, response, auth);
    }
}
