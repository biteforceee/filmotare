package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre findById(long id) {
        return genreStorage.getById(id).orElseThrow(() -> new NotFoundException("жанр с id: " + id + " не найден"));
    }

    public Genre create(Genre genre) {
        return genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        return genreStorage.update(genre);
    }

    public void delete(Genre genre) {
        genreStorage.delete(genre);
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Map<Long, Genre> findAllAsMap() {
        return genreStorage.findAllAsMap();
    }
}
