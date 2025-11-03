package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(Long id, Long userId) {
        userStorage.getById(userId);
        filmStorage.getById(id).getUsersLikes().add(userId);
    }

    public void deleteLike(Long id, Long userId) {
        userStorage.getById(userId);
        filmStorage.getById(id).getUsersLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.findAll().stream()
                .filter(film -> !film.getUsersLikes().isEmpty())
                .sorted(Comparator.comparing(film -> -film.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
