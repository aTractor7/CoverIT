package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.util.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void init() {
        testUser = TestDataFactory.getUser();
    }

    @Nested
    @DisplayName("Find methods")
    class FindTests {

        @Test
        void shouldReturnUser_WhenIdExists() {
            given(userRepository.findById(1)).willReturn(Optional.of(testUser));

            User result = userService.findOne(1);

            assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        void shouldThrowException_WhenUserIdNotFound() {
            int id = 99;
            given(userRepository.findById(id)).willReturn(Optional.empty());
            when(errorMessageService.getErrorMessage("user.notfound.byId", id))
                    .thenReturn("User with id " + id + " not found");

            assertThatThrownBy(() -> userService.findOne(99))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User with id " + id + " not found");
        }

        @Test
        void shouldReturnUser_WhenUsernameExists() {
            given(userRepository.findByUsername(testUser.getUsername())).willReturn(Optional.of(testUser));

            User result = userService.findOne(testUser.getUsername());

            assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        }

        @Test
        void shouldThrowException_WhenUsernameNotFound() {
            String username = "nope";
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());
            when(errorMessageService.getErrorMessage("user.notfound.byUsername", username))
                    .thenReturn("User with username " + username + " not found");

            assertThatThrownBy(() -> userService.findOne("nope"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("User with username " + username + " not found");
        }
    }

    @Nested
    @DisplayName("Register & update methods")
    class RegisterUpdateTests {

        @Test
        void shouldRegisterUser_WithEncodedPasswordAndDefaultRole() {
            given(passwordEncoder.encode(testUser.getPassword())).willReturn("encodedPass");

            userService.create(testUser);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            then(userRepository).should().save(captor.capture());
            User saved = captor.getValue();

            assertThat(saved.getPassword()).isEqualTo("encodedPass");
            assertThat(saved.getRole()).isEqualTo(Role.USER);
            assertThat(saved.getJoinDate()).isEqualTo(LocalDate.now());
        }

        @Test
        void shouldUpdateFields_WhenUserExists() {
            User updated = new User();
            updated.setUsername("newUser");
            updated.setEmail("new@mail.com");
            updated.setRole(Role.ADMIN);

            given(userRepository.findById(1)).willReturn(Optional.of(testUser));

            User result = userService.update(1, updated);

            assertThat(result.getUsername()).isEqualTo("newUser");
            assertThat(result.getEmail()).isEqualTo("new@mail.com");
            assertThat(result.getRole()).isEqualTo(Role.USER);
        }

        @Test
        void shouldEncodePassword_WhenUpdatingPassword() {
            given(userRepository.findById(1)).willReturn(Optional.of(testUser));
            given(passwordEncoder.encode("newpass")).willReturn("hashed");

            userService.updatePassword(1, "newpass");

            assertThat(testUser.getPassword()).isEqualTo("hashed");
        }
    }

    @Test
    void shouldDeleteUserById() {
        userService.delete(1);

        then(userRepository).should().deleteById(1);
    }

    @Test
    void shouldReturnTrue_WhenPasswordMatches() {
        given(passwordEncoder.matches("1234", "hashed")).willReturn(true);

        boolean result = userService.matchPassword("1234", "hashed");

        assertThat(result).isTrue();
    }
}
