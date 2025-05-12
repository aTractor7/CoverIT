package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.dto.ChordDto;
import com.example.GuitarApp.services.ChordService;
import com.example.GuitarApp.services.ErrorMessageService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER"})
public class ChordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChordService chordService;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private ModelMapper modelMapper;

    private Chord testChord;
    private ChordDto testChordDto;

    @BeforeEach
    void setUp() {
        testChord = new Chord();
        testChord.setId(1);
        testChord.setName("C Major");

        testChordDto = new ChordDto();
        testChordDto.setId(1);
        testChordDto.setName("C Major");
    }

    @Test
    void testGetAllChords() throws Exception {
        when(chordService.findPage(0, 10, Optional.empty())).thenReturn(List.of(testChord));
        when(modelMapper.map(testChord, ChordDto.class)).thenReturn(testChordDto);

        mockMvc.perform(get("/chords")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("C Major"));
    }

    @Test
    void testGetChordById() throws Exception {
        when(chordService.findOne(1)).thenReturn(testChord);
        when(modelMapper.map(testChord, ChordDto.class)).thenReturn(testChordDto);

        mockMvc.perform(get("/chords/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("C Major"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateChord() throws Exception {
        when(modelMapper.map(any(ChordDto.class), eq(Chord.class))).thenReturn(testChord);
        when(chordService.create(any(Chord.class))).thenReturn(testChord);
        when(modelMapper.map(any(Chord.class), eq(ChordDto.class))).thenReturn(testChordDto);

        mockMvc.perform(post("/chords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testChordDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/chords/1"))
                .andExpect(jsonPath("$.name").value("C Major"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateChord() throws Exception {
        when(modelMapper.map(any(ChordDto.class), eq(Chord.class))).thenReturn(testChord);
        when(chordService.update(eq(1), any(Chord.class))).thenReturn(testChord);
        when(modelMapper.map(any(Chord.class), eq(ChordDto.class))).thenReturn(testChordDto);

        mockMvc.perform(put("/chords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testChordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("C Major"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteChord() throws Exception {
        doNothing().when(chordService).delete(1);
        when(errorMessageService.getErrorMessage("chord.deleted")).thenReturn("Chord deleted");

        mockMvc.perform(delete("/chords/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Chord deleted"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateChordWithInvalidData() throws Exception {
        ChordDto invalidDto = new ChordDto();
        mockMvc.perform(post("/chords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Chord validation exception"));
    }
}
