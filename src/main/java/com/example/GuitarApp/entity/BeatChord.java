package com.example.GuitarApp.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beat_chords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BeatChord implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_beat_id", referencedColumnName = "id", nullable = false)
    private SongBeat songBeat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chord_id", referencedColumnName = "id", nullable = false)
    private Chord chord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_fingering_id", referencedColumnName = "id")
    private Fingering recommendedFingering;
}
