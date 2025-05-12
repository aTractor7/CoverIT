package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.*;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.SongTutorialService;
import com.example.GuitarApp.services.authorization.impl.ArtistAuthorizationService;
import com.example.GuitarApp.services.authorization.impl.SongTutorialAuthorizationService;
import com.example.GuitarApp.util.TestDataFactory;
import com.example.GuitarApp.util.validators.SongTutorialValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
public class SongTutorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SongTutorialService songTutorialService;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private SongTutorialValidator songTutorialValidator;
    @MockitoBean
    private SongTutorialAuthorizationService authorizationService;

    @MockitoBean
    private ModelMapper modelMapper;

    private SongTutorial testTutorial;
    private SongTutorialDto testTutorialDto;
    private SongTutorialCreateDto testCreateDto;
    private SongTutorialShortDto testShortDto;

    @BeforeEach
    void setUp() {
        User user = TestDataFactory.getUser();
        testTutorial = TestDataFactory.getSongTutorial();
        testTutorial.setId(1);
        testTutorial.setTutorialAuthor(user);
        testTutorial.setSong(TestDataFactory.getSongWithAuthor());

        SongBeat songBeat = TestDataFactory.getSongBeat();
        testTutorial.setSongBeats(Set.of(songBeat));

        BeatChord beatChord = songBeat.getBeatChords().get(0);

        SongShortDto songShortDto = new SongShortDto();
        songShortDto.setId(testTutorial.getSong().getId());
        songShortDto.setTitle(testTutorial.getSong().getTitle());

        SongBeatDto songBeatDto = TestDataFactory.getSongBeatDto(songBeat, beatChord);
        songBeatDto.setSongTutorialId(testTutorial.getId());
        songBeatDto.setSongTutorialId(testTutorial.getId());

        testTutorialDto = new SongTutorialDto();
        testTutorialDto.setId(1);
        testTutorialDto.setSong(songShortDto);
        testTutorialDto.setDifficulty(testTutorial.getDifficulty());
        testTutorialDto.setBeats(Set.of(songBeatDto));

        testCreateDto = new SongTutorialCreateDto();
        testCreateDto.setSong(songShortDto);
        testCreateDto.setDifficulty(testTutorial.getDifficulty());

        SongBeatCreationDto songBeatCreationDto = TestDataFactory.getSongBeatCreationDto(songBeat, beatChord);
        testCreateDto.setBeats(Set.of(songBeatCreationDto));

        testShortDto = new SongTutorialShortDto();
        testShortDto.setId(1);
        testShortDto.setSong(songShortDto);
        UserDto userDto = new UserDto();
        userDto.setId(testTutorial.getTutorialAuthor().getId());
        testShortDto.setTutorialAuthor(userDto);
    }

    @Test
    void testGetAllTutorials() throws Exception {
        when(songTutorialService.findPage(0, 10, Optional.empty(), Optional.empty()))
                .thenReturn(List.of(testTutorial));
        when(modelMapper.map(eq(testTutorial), eq(SongTutorialShortDto.class)))
                .thenReturn(testShortDto);

        mockMvc.perform(get("/tutorials")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].song.title").value(testTutorial.getSong().getTitle()));
    }

    @Test
    void testGetOneTutorial() throws Exception {
        when(songTutorialService.findOne(1)).thenReturn(testTutorial);
        when(modelMapper.map(testTutorial, SongTutorialDto.class)).thenReturn(testTutorialDto);

        mockMvc.perform(get("/tutorials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateTutorial() throws Exception {
        when(modelMapper.map(any(SongTutorialCreateDto.class), eq(SongTutorial.class))).thenReturn(testTutorial);
        when(songTutorialService.create(any(SongTutorial.class))).thenReturn(testTutorial);
        when(modelMapper.map(any(SongTutorial.class), eq(SongTutorialCreateDto.class))).thenReturn(testCreateDto);

        mockMvc.perform(post("/tutorials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tutorials/1"))
                .andExpect(jsonPath("$.song.title").value(testTutorial.getSong().getTitle()));
    }

    @Test
    void testUpdateTutorial() throws Exception {
        when(authorizationService.canUpdate(1)).thenReturn(true);
        when(modelMapper.map(any(SongTutorialDto.class), eq(SongTutorial.class))).thenReturn(testTutorial);
        when(modelMapper.map(any(SongTutorialDto.class), eq(SongTutorialCreateDto.class))).thenReturn(testCreateDto);
        when(songTutorialService.update(eq(1), any(SongTutorial.class))).thenReturn(testTutorial);
        when(modelMapper.map(any(SongTutorial.class), eq(SongTutorialDto.class))).thenReturn(testTutorialDto);

        mockMvc.perform(put("/tutorials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTutorialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTutorial() throws Exception {
        when(authorizationService.canDelete(1)).thenReturn(true);
        when(errorMessageService.getErrorMessage("songTutorial.deleted")).thenReturn("Deleted");

        mockMvc.perform(delete("/tutorials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted"));
    }
}
