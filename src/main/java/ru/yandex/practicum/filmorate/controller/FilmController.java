package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private final LocalDate FIRST_FILM_DATE = LocalDate.of(1895,12,28);

    @GetMapping
    public Collection<Film> findAll() {
        log.trace("Получили все фильмы.");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.trace("Добавляем новый фильм.");
        // проверяем выполнение необходимых условий
        log.debug("Валидация фильма.");
        validate(film);
        // формируем дополнительные данные
        log.debug("Устанавливаем значение id у фильма.");
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.trace("Добавили новый фильм с id = {}.", film.getId());
        return film;
    }

    private void validate(Film film) {
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка с названием фильма.");
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if ((long) film.getDescription().length() > 200) {
            log.warn("Ошибка с описанием фильма.");
            throw new ConditionsNotMetException("Максимальная длина описания не может быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Ошибка с датой выхода фильма.");
            throw new ConditionsNotMetException("дата релиза — не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() != null && film.getDuration().isNegative()) {
            log.warn("Ошибка с продолжительностью фильма.");
            throw new ConditionsNotMetException("продолжительность фильма не должна быть отрицательным числом");
        }
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.trace("Обновляем данные фильма.");
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            log.warn("Id должен быть указан.");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Валидация фильма.");
            validate(newFilm);
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            if (newFilm.getName() != null) {
                log.debug("Обновили имя фильма.");
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                log.debug("Обновили описание фильма.");
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                log.debug("Обновили дату выхода фильма.");
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                log.debug("Обновили продолжительность фильма.");
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.trace("Данные фильма обновлены.");
            return oldFilm;
        }
        log.warn("Фильм с id = {} не найден.", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
