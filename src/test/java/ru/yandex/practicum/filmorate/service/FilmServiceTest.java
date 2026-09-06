package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmServiceTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmService service = new FilmService(new InMemoryFilmStorage(), userStorage);
    private final UserService userService = new UserService(userStorage);
    Film film1, film2, badName, badDate, badDuration;
    User user1, user2, user3;

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
                120L
        );

        badName = new Film(null,
                "    ",
                "badName",
                LocalDate.of(2000, 2, 2),
                120L
        );

        badDate = new Film(null,
                "badDescription",
                "123",
                LocalDate.of(1895, 11, 2),
                120L
        );

        badDuration = new Film(null,
                "badDescription",
                "123",
                LocalDate.of(1895, 12, 30),
                -10L
        );


        user1 = new User(
                null,
                "user1",
                "user1@email.com",
                "name1",
                LocalDate.of(2000, 2, 2)
        );

        user2 = new User(
                null,
                "user2",
                "user2@email.com",
                "name2",
                LocalDate.of(2003, 2, 2)
        );

        user3 = new User(
                null,
                "user3",
                "user3@email.com",
                "name3",
                LocalDate.of(2003, 2, 2)
        );
    }

    @Test
    void findAll() {
        service.create(film1);
        List<Film> films = service.findAll().stream().toList();
        assertEquals(1, films.size());
        service.create(film2);
        films = service.findAll().stream().toList();
        assertEquals(2, films.size());
    }

    @Test
    void create() {
        Film newFilm = service.create(film1);
        assertNotNull(newFilm);
        assertEquals(film1, newFilm);
        assertThrows(ConditionsNotMetException.class, () -> service.create(badDate));
        assertThrows(ConditionsNotMetException.class, () -> service.create(badName));
        assertThrows(ConditionsNotMetException.class, () -> service.create(badDuration));
    }

    @Test
    void update() {
        Film newFilm = service.create(film1);
        assertEquals(film1, newFilm);
        newFilm.setDuration(-1L);
        assertThrows(ConditionsNotMetException.class, () -> service.update(newFilm));
        newFilm.setDuration(120L);
        newFilm.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ConditionsNotMetException.class, () -> service.update(newFilm));
        newFilm.setReleaseDate(LocalDate.of(2000,1,1));
        newFilm.setName("     ");
        assertThrows(ConditionsNotMetException.class, () -> service.update(newFilm));
        newFilm.setName("123");
        Film film = service.update(newFilm);
        assertEquals(newFilm, film);
    }

    @Test
    void addLike() {
        User user = userService.create(user1);
        Film film = service.create(film1);

        assertEquals(Set.of(), film.getUsersLikes());

        service.addLike(film.getId(), user.getId());

        assertEquals(Set.of(user.getId()), film.getUsersLikes());
    }

    @Test
    void deleteLike() {
        User user = userService.create(user1);
        Film film = service.create(film1);

        assertEquals(Set.of(), film.getUsersLikes());
        service.addLike(film.getId(), user.getId());
        assertEquals(Set.of(user.getId()), film.getUsersLikes());
        service.deleteLike(film.getId(), user.getId());
        assertEquals(Set.of(), film.getUsersLikes());
    }

    @Test
    void getPopularFilms() {
        User newUser1 = userService.create(user1);
        User newUser2 = userService.create(user2);
        User newUser3 = userService.create(user3);
        Film newFilm1 = service.create(film1);
        Film newFilm2 = service.create(film2);

        service.addLike(newFilm1.getId(), newUser1.getId());
        service.addLike(newFilm1.getId(), newUser2.getId());
        service.addLike(newFilm1.getId(), newUser3.getId());

        assertEquals(List.of(newFilm1), service.getPopularFilms(10L));

        service.addLike(newFilm2.getId(), newUser1.getId());

        assertEquals(List.of(newFilm1), service.getPopularFilms(1L));
        assertEquals(List.of(newFilm1, newFilm2), service.getPopularFilms(10L));
    }
}