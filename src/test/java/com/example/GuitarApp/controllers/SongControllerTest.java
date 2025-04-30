package com.example.GuitarApp.controllers;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.dto.ArtistShortDto;
import com.example.GuitarApp.entity.dto.SongDto;
import com.example.GuitarApp.entity.dto.SongShortDto;
import com.example.GuitarApp.repositories.SongRepository;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.SongService;
import com.example.GuitarApp.services.authorization.impl.SongAuthorizationService;
import com.example.GuitarApp.services.authorization.impl.UserAuthorizationService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER"})
public class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ModelMapper modelMapper;

    @MockitoBean
    private SongService songService;

    @MockitoBean
    private SongRepository songRepository;

    @MockitoBean
    private SongAuthorizationService authorizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ErrorMessageService errorMessageService;

    private Song testSong;
    private SongDto testSongDto;

    @BeforeEach
    void setUp() {
        testSong = TestDataFactory.getSongWithAuthor();
        testSong.setCreatedBy(TestDataFactory.getUser());

        testSongDto = new SongDto();
        testSongDto.setTitle(testSong.getTitle());
        testSongDto.setGenre(testSong.getGenre());
        testSongDto.setCreator_id(testSong.getCreatedBy().getId());
        testSongDto.setReleaseDate(testSong.getReleaseDate());

        ArtistShortDto artistShortDto = new ArtistShortDto();
        Artist artist = testSong.getSongAuthors().stream().findFirst().get();
        artistShortDto.setId(artist.getId());
        artistShortDto.setName(artist.getName());

        testSongDto.setSongAuthors(Set.of(artistShortDto));
    }

    @Test
    void testGetAllSongs() throws Exception {
        when(songService.findPage(0, 10, Optional.empty())).thenReturn(List.of(testSong));

        SongDto songDto = new SongDto();
        songDto.setTitle(testSong.getTitle());

        when(modelMapper.map(any(Song.class), eq(SongDto.class))).thenReturn(songDto);

        mockMvc.perform(get("/songs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value(testSong.getTitle()));
    }

    @Test
    void testGetSongById() throws Exception {
        SongDto songDto = new SongDto();
        songDto.setId(testSong.getId());
        songDto.setTitle(testSong.getTitle());
        when(songService.findOne(1)).thenReturn(testSong);
        when(modelMapper.map(testSong, SongDto.class)).thenReturn(songDto);

        mockMvc.perform(get("/songs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(songDto.getId()));
    }

    @Test
    void testSaveSong() throws Exception {
        when(modelMapper.map(any(SongDto.class), eq(Song.class))).thenReturn(testSong);
        when(songService.create(any(Song.class))).thenReturn(testSong);

        mockMvc.perform(post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSongDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/songs/0"))
                .andExpect(jsonPath("$.title").value(testSong.getTitle()));
    }

    @Test
    void testUpdateSong() throws Exception {
        when(authorizationService.canUpdate(1)).thenReturn(true);
        when(songService.update(eq(1), any(Song.class))).thenReturn(testSong);
        when(modelMapper.map(any(SongDto.class), eq(Song.class))).thenReturn(testSong);
        when(modelMapper.map(any(Song.class), eq(SongDto.class))).thenReturn(testSongDto);

        mockMvc.perform(put("/songs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSongDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testSongDto.getTitle()));
    }

    @Test
    void testDeleteSong() throws Exception {
        when(authorizationService.canDelete(1)).thenReturn(true);
        doNothing().when(songService).delete(1);

        mockMvc.perform(delete("/songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Song deleted successfully"));
    }
}
