package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<Collection<Genre>> findAll() {
        return new ResponseEntity<>(genreService.findAll(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> findById(@PathVariable Long id) {
        return new ResponseEntity<>(genreService.findById(id), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Genre> create(@RequestBody Genre genre) {
        return new ResponseEntity<>(genreService.create(genre), HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<Genre> update(@RequestBody Genre newGenre) {
        return new ResponseEntity<>(genreService.update(newGenre), HttpStatusCode.valueOf(200));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody Genre genre) {
        genreService.delete(genre);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }
}
