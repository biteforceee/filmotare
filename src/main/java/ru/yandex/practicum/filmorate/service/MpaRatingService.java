package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public MpaRating findById(long id) {
        return mpaRatingStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("рейтинг с id: " + id + " не найден"));
    }

    public MpaRating create(MpaRating mpaRating) {
        return mpaRatingStorage.create(mpaRating);
    }

    public MpaRating update(MpaRating mpaRating) {
        return mpaRatingStorage.update(mpaRating);
    }

    public void delete(MpaRating mpaRating) {
        mpaRatingStorage.delete(mpaRating);
    }

    public Collection<MpaRating> findAll() {
        return mpaRatingStorage.findAll();
    }

    public Map<Long, MpaRating> findAllAsMap() {
        return mpaRatingStorage.findAllAsMap();
    }
}
