package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.Set;
import java.util.stream.Collectors;

public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDuration(request.getDuration());
        film.setReleaseDate(request.getReleaseDate());
        film.setDescription(request.getDescription());
        film.setRatingId(request.getRatingId());
        film.setGenres(request.getGenres());

        return film;
    }

    public static FilmDto mapToFilmDto(Film film, MpaRating rating, Set<Genre> genres) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDescription(film.getDescription());
        filmDto.setMpaRating(rating.getName());
        filmDto.setGenres(genres.stream().map(Genre::getName).collect(Collectors.toSet()));
        filmDto.setLikesCount((long) film.getUsersLikes().size());

        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasRating()) {
            film.setRatingId(request.getRatingId());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }

        return film;
    }
}
