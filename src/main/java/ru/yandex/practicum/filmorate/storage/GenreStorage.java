package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> findAll();

    Genre create(Genre genre);

    Genre update(Genre newGenre);

    void delete(Genre genre);

    Optional<Genre> getById(Long id);

    Map<Long, Genre> findAllAsMap();
}
