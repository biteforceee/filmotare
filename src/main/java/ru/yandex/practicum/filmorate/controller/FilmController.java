package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        return new ResponseEntity<>(filmService.findAll(), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<FilmDto> create(@RequestBody NewFilmRequest film) {
        return new ResponseEntity<>(filmService.create(film), HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<FilmDto> update(@RequestBody UpdateFilmRequest newFilm) {
        return new ResponseEntity<>(filmService.update(newFilm), HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDto>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Long count) {
        return new ResponseEntity<>(filmService.getPopularFilms(count), HttpStatusCode.valueOf(200));
    }

}
