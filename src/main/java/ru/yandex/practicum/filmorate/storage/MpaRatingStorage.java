package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface MpaRatingStorage {
    Collection<MpaRating> findAll();

    MpaRating create(MpaRating mpaRating);

    MpaRating update(MpaRating newMpaRating);

    void delete(MpaRating mpaRating);

    Optional<MpaRating> getById(Long id);

    Map<Long, MpaRating> findAllAsMap();
}
