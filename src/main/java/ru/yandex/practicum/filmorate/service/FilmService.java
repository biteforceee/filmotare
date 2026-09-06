package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    private final GenreService genreService;
    private final MpaRatingService mpaRatingService;

    public FilmDto create(NewFilmRequest newFilmRequest) {
        MpaRating mpaRating = mpaRatingService.findById(newFilmRequest.getRatingId());

        Set<Genre> genres = newFilmRequest.getGenres().stream()
                .map(genreService::findById)
                .collect(Collectors.toSet());

        Film film = filmStorage.create(FilmMapper.mapToFilm(newFilmRequest));

        return FilmMapper.mapToFilmDto(film, mpaRating, genres);
    }

    public FilmDto update(UpdateFilmRequest updateFilmRequest) {
        MpaRating mpaRating = mpaRatingService.findById(updateFilmRequest.getRatingId());

        Set<Genre> genres = updateFilmRequest.getGenres().stream()
                .map(genreService::findById)
                .collect(Collectors.toSet());

        Film film = filmStorage.getById(updateFilmRequest.getId());
        Film updatedFilm = filmStorage.update(FilmMapper.updateFilmFields(film, updateFilmRequest));

        return FilmMapper.mapToFilmDto(updatedFilm, mpaRating, genres);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public Collection<FilmDto> findAll() {
        Collection<Film> films = filmStorage.findAll();
        if (films.isEmpty()) return Collections.emptyList();

        Map<Long, Genre> allGenres = genreService.findAllAsMap();

        Map<Long, MpaRating> allMpaRatings = mpaRatingService.findAllAsMap();

        return films.stream()
                .map(film -> {
                    MpaRating mpaRating = allMpaRatings.get(film.getRatingId());

                    Set<Genre> genres = film.getGenres().stream()
                            .map(allGenres::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    return FilmMapper.mapToFilmDto(film, mpaRating, genres);
                })
                .toList();
    }

    public void addLike(Long id, Long userId) {
        userStorage.getById(userId);
        Film film = filmStorage.getById(id);
        film.getUsersLikes().add(userId);
        filmStorage.update(film);
    }

    public void deleteLike(Long id, Long userId) {
        userStorage.getById(userId);
        Film film = filmStorage.getById(id);
        film.getUsersLikes().remove(userId);
        filmStorage.update(film);
    }

    public List<FilmDto> getPopularFilms(Long count) {
        return findAll().stream()
                .filter(film -> film.getLikesCount() != 0)
                .sorted(Comparator.comparing(film -> -film.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());

//        return findAll().stream()
//                .filter(film -> !film.getUsersLikes().isEmpty())
//                .sorted(Comparator.comparing(film -> -film.getUsersLikes().size()))
//                .limit(count)
//                .collect(Collectors.toList());
    }
}
