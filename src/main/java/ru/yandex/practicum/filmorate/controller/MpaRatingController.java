package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {
    private final MpaRatingService mpaRatingService;

    @GetMapping
    public ResponseEntity<Collection<MpaRating>> findAll() {
        return new ResponseEntity<>(mpaRatingService.findAll(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> findById(@PathVariable Long id) {
        return new ResponseEntity<>(mpaRatingService.findById(id), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<MpaRating> create(@RequestBody MpaRating mpaRating) {
        return new ResponseEntity<>(mpaRatingService.create(mpaRating), HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<MpaRating> update(@RequestBody MpaRating newMpaRating) {
        return new ResponseEntity<>(mpaRatingService.update(newMpaRating), HttpStatusCode.valueOf(200));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody MpaRating mpaRating) {
        mpaRatingService.delete(mpaRating);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }
}
