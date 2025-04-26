package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.services.AuthorizationService;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ModelMapper modelMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthorizationService authorizationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ErrorMessageService errorMessageService;

    private User testUser;

    @BeforeEach
    void setUp() {
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);

        testUser = TestDataFactory.getUser();
        testUser.setId(1);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.findPage(0, 10, Optional.empty())).thenReturn(List.of(testUser));

        UserDto userDto = new UserDto();
        userDto.setUsername(testUser.getUsername());

        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value(testUser.getUsername()));
    }

    @Test
    void testGetUserById() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        when(userService.findOne(1)).thenReturn(testUser);
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        userDto.setUsername(testUser.getUsername());
        userDto.setEmail(testUser.getEmail());
        userDto.setSkill(testUser.getSkill());

        when(authorizationService.canUpdateUser(1)).thenReturn(true);
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(testUser);
        when(userService.update(eq(1), any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);
        when(userRepository.existsByUsernameAndIdNot(eq(userDto.getUsername()), anyInt())).thenReturn(false);
        when(userRepository.existsByEmailAndIdNot(eq(userDto.getEmail()), anyInt())).thenReturn(false);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userDto.getUsername()));
    }

    @Test
    void updateUser_whenUsernameAndEmailAlreadyExists_thenReturnsValidationError() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(testUser.getId());
        userDto.setUsername(testUser.getUsername());
        userDto.setEmail(testUser.getEmail());
        userDto.setSkill(testUser.getSkill());


        when(authorizationService.canUpdateUser(1)).thenReturn(true);
        when(userRepository.existsByUsernameAndIdNot(eq(userDto.getUsername()), anyInt())).thenReturn(true);
        when(userRepository.existsByEmailAndIdNot(eq(userDto.getEmail()), anyInt())).thenReturn(true);

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("username")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("email")));
    }

    @Test
    void testUpdatePassword() throws Exception {
        when(authorizationService.canUpdateUser(1)).thenReturn(true);
        when(userService.matchPassword(any(), any())).thenReturn(true);

        mockMvc.perform(put("/users/1/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"oldPassword\", \"newPassword\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(errorMessageService.getErrorMessage("user.password_changed")));
    }

    @Test
    void testHandlePasswordChangeValidationException() throws Exception {
        when(authorizationService.canUpdateUser(1)).thenReturn(true);
        when(userService.matchPassword(any(), any())).thenReturn(true);

        mockMvc.perform(put("/users/1/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"oldPassword\", \"newPassword\": \"oldPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password update exception"))
                .andExpect(jsonPath("$.message").value("error caused by field oldPassword: "
                        + errorMessageService.getErrorMessage("validation.not_equals_passwords") + ";  "));
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        when(userService.findOne(1)).thenThrow(new IllegalArgumentException("Invalid user data"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User update exception"))
                .andExpect(jsonPath("$.message").value("Invalid user data"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1);

        when(authorizationService.canDeleteUser(1)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
