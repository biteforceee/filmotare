package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.*;

@Repository("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, duration, release_date, rating_id)" +
            "VALUES (?, ?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    private static final String FIND_LIKES_QUERY = "SELECT film_id, user_id FROM likes";
    private static final String FIND_LIKES_BY_ID_QUERY = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_LIKES_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?) ON CONFLICT (film_id, user_id) DO NOTHING";
    private static final String DELETE_LIKES_BY_FILM_ID_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String DELETE_SINGLE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_GENRES_QUERY = "SELECT film_id, genre_id FROM film_genres";
    private static final String FIND_GENRES_BY_ID_QUERY = "SELECT genre_id FROM film_genres WHERE film_id = ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?) ON CONFLICT (film_id, genre_id) DO NOTHING";
    private static final String DELETE_GENRES_BY_FILM_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String DELETE_SINGLE_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);

        Map<Long, Set<Long>> likes = jdbcTemplate.query(FIND_LIKES_QUERY, rs -> {
            HashMap<Long, Set<Long>> map = new HashMap<>();
            while (rs.next()){
                Long userId = rs.getLong("user_id");
                Long filmId = rs.getLong("film_id");
                map.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            }
            return map;
        });

        Map<Long, Set<Long>> genres = jdbcTemplate.query(FIND_GENRES_QUERY, rs -> {
            HashMap<Long, Set<Long>> map = new HashMap<>();
            while (rs.next()){
                Long genreId = rs.getLong("genre_id");
                Long filmId = rs.getLong("film_id");
                map.computeIfAbsent(filmId, k -> new HashSet<>()).add(genreId);
            }
            return map;
        });

        films.forEach(film -> {
            film.setUsersLikes(likes.getOrDefault(film.getId(), Collections.emptySet()));
            film.setGenres(genres.getOrDefault(film.getId(), Collections.emptySet()));
        });
        return films;
    }

    @Override
    public Film getById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .map(film -> {
                    film.setUsersLikes(new HashSet<>(jdbcTemplate.query(FIND_LIKES_BY_ID_QUERY, (rs, rowNum) -> rs.getLong("user_id"), id)));
                    film.setGenres(new HashSet<>(jdbcTemplate.query(FIND_GENRES_BY_ID_QUERY, (rs, rowNum) -> rs.getLong("genre_id"), id)));
                    return film;
                })
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getRatingId()
        );

        film.setId(id);

        syncGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getRatingId(),
                film.getId());

        syncGenres(film);
        syncLikes(film);

        return film;
    }

    @Override
    public void delete(Film film) {
        delete(DELETE_QUERY, film.getId());
    }

    private void syncGenres(Film film) {
        syncRelatedIds(
                FIND_GENRES_BY_ID_QUERY,
                INSERT_GENRE_QUERY,
                DELETE_SINGLE_GENRE_QUERY,
                film.getId(),
                film.getGenres(),
                "genre_id"
        );
    }

    private void syncLikes(Film film) {
        syncRelatedIds(
                FIND_LIKES_BY_ID_QUERY,
                INSERT_LIKES_QUERY,
                DELETE_SINGLE_LIKE_QUERY,
                film.getId(),
                film.getUsersLikes(),
                "user_id"
        );
    }
}
