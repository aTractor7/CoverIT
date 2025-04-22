package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findPage(int page, int pageSize, Optional<String> sortField) {
        Pageable pageable = sortField
                .map(field -> PageRequest.of(page, pageSize, Sort.by(field)))
                .orElseGet(() -> PageRequest.of(page, pageSize));

        return userRepository.findAll(pageable).getContent();
    }

    public User findOne(int id) {
        return userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User findOne(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }

    public boolean matchPassword(String checked, String encodedPassword) {
        return passwordEncoder.matches(checked, encodedPassword);
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setJoinDate(LocalDate.now());
        userRepository.save(user);
    }

    @Transactional
    public User update(int id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user with id: " + id));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setSkill(updatedUser.getSkill());
        user.setInstrument(updatedUser.getInstrument());
        user.setBio(updatedUser.getBio());
        user.setProfileImg(updatedUser.getProfileImg());

        return user;
    }

    @Transactional
    public void updatePassword(int id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user with id: " + id));

        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
