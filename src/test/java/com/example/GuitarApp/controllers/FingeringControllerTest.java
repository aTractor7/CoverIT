package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.entity.dto.ChordShortDto;
import com.example.GuitarApp.entity.dto.FingeringDto;
import com.example.GuitarApp.repositories.ChordRepository;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.FingeringsService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = {"USER"})
public class FingeringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FingeringsService fingeringsService;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private ChordRepository chordRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    private Fingering testFingering;
    private FingeringDto testFingeringDto;

    @BeforeEach
    void setUp() {
        Chord testChord = new Chord();
        testChord.setId(1);
        testChord.setName("C Major");

        ChordShortDto chordShortDto = new ChordShortDto();
        chordShortDto.setId(testChord.getId());
        chordShortDto.setName(testChord.getName());

        testFingering = new Fingering();
        testFingering.setId(1);
        testFingering.setImgPath("/images/fingering.jpg");
        testFingering.setChord(testChord);

        testFingeringDto = new FingeringDto();
        testFingeringDto.setId(1);
        testFingeringDto.setImgPath("/images/fingering2.jpg");
        testFingeringDto.setChord(chordShortDto);
    }

    @Test
    void testGetAllFingerings() throws Exception {
        when(fingeringsService.findPage(0, 10, Optional.empty())).thenReturn(List.of(testFingering));
        when(modelMapper.map(testFingering, FingeringDto.class)).thenReturn(testFingeringDto);

        mockMvc.perform(get("/fingerings")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetFingeringById() throws Exception {
        when(fingeringsService.findOne(1)).thenReturn(testFingering);
        when(modelMapper.map(testFingering, FingeringDto.class)).thenReturn(testFingeringDto);

        mockMvc.perform(get("/fingerings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateFingering() throws Exception {
        when(modelMapper.map(any(FingeringDto.class), eq(Fingering.class))).thenReturn(testFingering);
        when(fingeringsService.create(any(Fingering.class))).thenReturn(testFingering);
        when(modelMapper.map(any(Fingering.class), eq(FingeringDto.class))).thenReturn(testFingeringDto);
        when(chordRepository.findByName(testFingering.getChord().getName())).thenReturn(Optional.of(testFingering.getChord()));

        mockMvc.perform(post("/fingerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFingeringDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/fingerings/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateFingering() throws Exception {
        when(modelMapper.map(any(FingeringDto.class), eq(Fingering.class))).thenReturn(testFingering);
        when(fingeringsService.update(eq(1), any(Fingering.class))).thenReturn(testFingering);
        when(modelMapper.map(any(Fingering.class), eq(FingeringDto.class))).thenReturn(testFingeringDto);
        when(chordRepository.findByName(testFingering.getChord().getName())).thenReturn(Optional.of(testFingering.getChord()));

        mockMvc.perform(put("/fingerings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFingeringDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteFingering() throws Exception {
        doNothing().when(fingeringsService).delete(1);
        when(errorMessageService.getErrorMessage("fingering.deleted")).thenReturn("Fingering deleted");

        mockMvc.perform(delete("/fingerings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Fingering deleted"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateFingeringWithInvalidData() throws Exception {
        FingeringDto invalidDto = new FingeringDto(); // без обов’язкових полів
        mockMvc.perform(post("/fingerings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Fingering validation exception"));
    }
}
