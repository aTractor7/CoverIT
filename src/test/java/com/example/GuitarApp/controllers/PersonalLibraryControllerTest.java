package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.PersonalLibraryCreateDto;
import com.example.GuitarApp.entity.dto.PersonalLibraryDto;
import com.example.GuitarApp.entity.dto.SongTutorialShortDto;
import com.example.GuitarApp.entity.dto.UserDto;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.PersonalLibraryService;
import com.example.GuitarApp.util.TestDataFactory;
import com.example.GuitarApp.util.validators.PersonalLibraryValidator;
import com.example.GuitarApp.util.validators.SongTutorialValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
public class PersonalLibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonalLibraryService personalLibraryService;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private PersonalLibraryValidator personalLibraryValidator;

    @MockitoBean
    private ModelMapper modelMapper;

    private PersonalLibrary testLibrary;
    private PersonalLibraryDto testLibraryDto;
    private PersonalLibraryCreateDto testCreateDto;

    @BeforeEach
    void setUp() {
        User user = TestDataFactory.getUser();
        SongTutorial tutorial = TestDataFactory.getSongTutorial();

        testLibrary = TestDataFactory.getPersonalLibrary();
        testLibrary.setId(1);
        testLibrary.setOwner(user);
        testLibrary.setSongTutorial(tutorial);

        testCreateDto = new PersonalLibraryCreateDto(tutorial.getId());

        testLibraryDto = new PersonalLibraryDto();
        testLibraryDto.setId(testLibrary.getId());
        testLibraryDto.setAddDate(testLibrary.getAddDate());
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        testLibraryDto.setOwner(userDto);
        SongTutorialShortDto tutorialShortDto = new SongTutorialShortDto();
        tutorialShortDto.setId(tutorial.getId());
        testLibraryDto.setSongTutorial(tutorialShortDto);
    }

    @Test
    void testGetAllLibraries() throws Exception {
        when(personalLibraryService.findPage(0, 10, Optional.empty(), Optional.empty()))
                .thenReturn(List.of(testLibrary));
        when(modelMapper.map(any(PersonalLibrary.class), eq(PersonalLibraryDto.class)))
                .thenReturn(testLibraryDto);

        mockMvc.perform(get("/personal-library")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetOneLibrary() throws Exception {
        when(personalLibraryService.findOne(1)).thenReturn(testLibrary);
        when(modelMapper.map(testLibrary, PersonalLibraryDto.class)).thenReturn(testLibraryDto);

        mockMvc.perform(get("/personal-library/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testSaveLibrary() throws Exception {
        when(personalLibraryService.create(any(PersonalLibrary.class))).thenReturn(testLibrary);

        mockMvc.perform(post("/personal-library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/personal-library/1"))
                .andExpect(jsonPath("$.tutorialId").value(0));
    }

    @Test
    void testDeleteLibrary() throws Exception {
        doNothing().when(personalLibraryService).deleteByTutorialId(1);
        when(errorMessageService.getErrorMessage("personalLibrary.deleted"))
                .thenReturn("Library deleted");

        mockMvc.perform(delete("/personal-library")
                        .param("tutorialId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Library deleted"));
    }

    @Test
    void testSaveLibraryWithInvalidData() throws Exception {
        PersonalLibraryCreateDto invalidDto = new PersonalLibraryCreateDto();

        mockMvc.perform(post("/personal-library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Personal library validation exception"));
    }
}