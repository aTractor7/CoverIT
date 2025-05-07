package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.ArtistDto;
import com.example.GuitarApp.entity.dto.ArtistCreateDto;
import com.example.GuitarApp.services.ArtistService;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.authorization.impl.ArtistAuthorizationService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistService artistService;

    @MockitoBean
    private ErrorMessageService errorMessageService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private ArtistAuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Artist artist;
    private ArtistDto artistDto;
    private ArtistCreateDto artistCreateDto;

    @BeforeEach
    void setUp() {
        artist = new Artist();
        artist.setId(1);
        artist.setName("Test Artist");
        artist.setSongs(Set.of());

        User creator = new User();
        creator.setId(99);
        artist.setCreatedBy(creator);

        artistDto = new ArtistDto();
        artistDto.setId(1);
        artistDto.setName("Test Artist");
        artistDto.setCreator_id(99);
        artistDto.setSongs(Set.of());

        artistCreateDto = new ArtistCreateDto();
        artistCreateDto.setName("Test Artist");
    }

    @Test
    void testGetAllPageable() throws Exception {
        when(artistService.findPage(0, 10, Optional.empty())).thenReturn(List.of(artist));
        when(modelMapper.map(any(Artist.class), eq(ArtistDto.class))).thenReturn(artistDto);

        mockMvc.perform(get("/artists")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Artist"));
    }

    @Test
    void testGetArtistById() throws Exception {
        when(artistService.findOne(1)).thenReturn(artist);
        when(modelMapper.map(artist, ArtistDto.class)).thenReturn(artistDto);

        mockMvc.perform(get("/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Artist"))
                .andExpect(jsonPath("$.creator_id").value(99));
    }


    @Test
    void testCreateArtist() throws Exception {
        when(modelMapper.map(any(ArtistCreateDto.class), eq(Artist.class))).thenReturn(artist);
        when(artistService.create(any(Artist.class))).thenReturn(artist);
        when(modelMapper.map(any(Artist.class), eq(ArtistCreateDto.class))).thenReturn(artistCreateDto);

        mockMvc.perform(post("/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Artist"));
    }

    @Test
    void testUpdateArtist() throws Exception {
        when(authorizationService.canUpdate(1)).thenReturn(true);
        when(modelMapper.map(any(ArtistCreateDto.class), eq(Artist.class))).thenReturn(artist);
        when(artistService.update(eq(1), any(Artist.class))).thenReturn(artist);
        when(modelMapper.map(any(Artist.class), eq(ArtistDto.class))).thenReturn(artistDto);

        mockMvc.perform(put("/artists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artistCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Artist"));
    }

    @Test
    void testDeleteArtist() throws Exception {
        when(authorizationService.canDelete(1)).thenReturn(true);
        doNothing().when(artistService).delete(1);
        when(errorMessageService.getErrorMessage("artist.deleted")).thenReturn("Artist deleted successfully");

        mockMvc.perform(delete("/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Artist deleted successfully"));
    }
}
