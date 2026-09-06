package ru.yandex.practicum.filmorate.model.film;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> usersLikes;
    private Set<Long> genres;
    private Long ratingId;

    public Film(Long id, String name, String description, LocalDate releaseDate, Long duration, Set<Long> genres, Long ratingId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.usersLikes = new HashSet<>();
        this.genres = genres;
        this.ratingId = ratingId;
    }
}