package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NonNull
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long ratingId;
    private Set<Long> genres;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasDuration() {
        return ! (duration == null || duration < 0L);
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasRating() {
        return ! (ratingId == null || ratingId < 0L);
    }

    public boolean hasGenres() {
        return genres != null;
    }
}
