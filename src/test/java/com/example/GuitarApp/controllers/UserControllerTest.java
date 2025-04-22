package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.entity.dto.ChangePasswordDto;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.exceptions.PasswordChangeValidationException;
import com.example.GuitarApp.util.validators.PasswordValidator;
import com.example.GuitarApp.util.TestDataFactory; // Імпортуємо фабрику

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        try {
            var mocks = MockitoAnnotations.openMocks(this);
            mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

            testUser = TestDataFactory.getUser();
            testUser.setId(1);

            mocks.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.findPage(0, 10, Optional.empty())).thenReturn(List.of(testUser));

        // Створимо UserDto із заповненим username, щоб перевірка пройшла
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
        userDto.setId(1);
        userDto.setUsername("John");
        userDto.setEmail("john@example.com");
        userDto.setSkill(Skill.BEGINNER);

        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(testUser);
        when(userService.update(eq(1), any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "John",
                                "email": "john@example.com",
                                "skill": "BEGINNER"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John"));
    }


    @Test
    void testUpdatePassword() throws Exception {
        doNothing().when(passwordValidator).validate(anyString(), anyString(), any(), any());

        mockMvc.perform(put("/users/1/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"oldPassword\", \"newPassword\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
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
    void testHandlePasswordChangeValidationException() throws Exception {
        doThrow(new PasswordChangeValidationException("Invalid password"))
                .when(userService).updatePassword(anyInt(), anyString());

        mockMvc.perform(put("/users/1/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\": \"oldPassword\", \"newPassword\": \"newPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Password update exception"))
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }
}
