package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.repositories.FingeringRepository;
import com.example.GuitarApp.util.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FingeringsServiceTest {

    @Mock
    private FingeringRepository fingeringRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @InjectMocks
    private FingeringsService fingeringsService;

    private Fingering testFingering;
    private final String imgPath = "ImgPath";

    @BeforeEach
    void setUp() {
        testFingering = new Fingering();
        testFingering.setId(1);
        testFingering.setImgPath(imgPath);
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnFingering_WhenIdExists() {
            given(fingeringRepository.findById(1)).willReturn(Optional.of(testFingering));

            Fingering result = fingeringsService.findOne(1);

            assertThat(result.getImgPath()).isEqualTo(imgPath);
        }

        @Test
        void shouldThrow_WhenFingeringIdNotFound() {
            int id = 99;
            given(fingeringRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("fingering.notfound.byId", id))
                    .willReturn("Fingering with id " + id + " not found");

            assertThatThrownBy(() -> fingeringsService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Fingering with id 99 not found");
        }

        @Test
        void shouldReturnPageOfFingerings() {
            Fingering second = new Fingering();
            second.setId(2);
            second.setImgPath("img2");

            Page<Fingering> page = new PageImpl<>(Arrays.asList(testFingering, second));
            Pageable pageable = PageRequest.of(0, 2, Sort.by("img"));

            given(fingeringRepository.findAll(pageable)).willReturn(page);

            List<Fingering> result = fingeringsService.findPage(0, 2, Optional.of("img"));

            assertThat(result).hasSize(2).extracting("imgPath")
                    .containsExactly(imgPath, "img2");
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateFingering() {
            given(fingeringRepository.save(testFingering)).willReturn(testFingering);

            Fingering result = fingeringsService.create(testFingering);

            assertThat(result.getImgPath()).isEqualTo(imgPath);
        }

        @Test
        void shouldUpdateFingeringFields_WhenExists() {
            Fingering updated = new Fingering();
            updated.setImgPath("updImg");

            given(fingeringRepository.findById(1)).willReturn(Optional.of(testFingering));

            Fingering result = fingeringsService.update(1, updated);

            assertThat(result.getImgPath()).isEqualTo("updImg");
        }

        @Test
        void shouldDeleteFingeringById() {
            fingeringsService.delete(1);

            then(fingeringRepository).should().deleteById(1);
        }
    }
}
