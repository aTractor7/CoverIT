package com.example.GuitarApp.entity;

import com.example.GuitarApp.entity.enums.SongGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "jams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Jam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SongGenre genre;

    // Тимчасово nullable (TODO: зробити not null пізніше)
    @Lob
    @Column(name = "audio", columnDefinition = "LONGBLOB")
    private byte[] audio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jam_key", referencedColumnName = "id", nullable = false)
    private Note jamKey;
}
