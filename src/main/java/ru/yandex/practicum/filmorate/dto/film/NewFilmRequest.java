package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long ratingId;
    private Set<Long> genres;
}
