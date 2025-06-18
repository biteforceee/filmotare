package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private final FilmController controller = new FilmController();
    Film film1, film2, badName, badDate, badDuration;

    @BeforeEach
    void setUp() {
        film1 = new Film(
                null,
                "film1",
                "film1 description",
                LocalDate.of(2000, 2, 2),
                null
        );

        film2 = new Film(null,
                "film2",
                "film2 description",
                LocalDate.of(2000, 2, 2),
                Duration.ofMinutes(120)
        );

        badName = new Film(null,
                "    ",
                "badName",
                LocalDate.of(2000, 2, 2),
                Duration.ofMinutes(120)
        );

        badDate = new Film(null,
                "badDescription",
                "123",
                LocalDate.of(1895, 11, 2),
                Duration.ofMinutes(120)
        );

        badDuration = new Film(null,
                "badDescription",
                "123",
                LocalDate.of(1895, 12, 30),
                Duration.ofMinutes(-10)
        );
    }

    @Test
    void findAll() {
        controller.create(film1);
        List<Film> films = controller.findAll().stream().toList();
        assertEquals(1, films.size());
        controller.create(film2);
        films = controller.findAll().stream().toList();
        assertEquals(2, films.size());
    }

    @Test
    void create() {
        Film newFilm = controller.create(film1);
        assertNotNull(newFilm);
        assertEquals(film1, newFilm);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badDate));
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badName));
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badDuration));
    }

    @Test
    void update() {
        Film newFilm = controller.create(film1);
        assertEquals(film1, newFilm);
        newFilm.setDuration(Duration.ofMinutes(-1));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newFilm));
        newFilm.setDuration(Duration.ofMinutes(120));
        newFilm.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newFilm));
        newFilm.setReleaseDate(LocalDate.of(2000,1,1));
        newFilm.setName("     ");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newFilm));
        newFilm.setName("123");
        Film film = controller.update(newFilm);
        assertEquals(newFilm, film);
    }
}