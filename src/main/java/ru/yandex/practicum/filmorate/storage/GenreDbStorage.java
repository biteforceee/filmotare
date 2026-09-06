package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.*;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres(name) VALUES (?) returning id";
    private static final String UPDATE_QUERY = "UPDATE genres SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getById(Long id) {
        return findOne(FIND_BY_ID_QUERY);
    }

    @Override
    public Genre create(Genre genre) {
        long id = insert(INSERT_QUERY, genre.getName());
        genre.setId(id);

        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        update(UPDATE_QUERY, genre.getName(), genre.getName());
        return genre;
    }

    @Override
    public void delete(Genre genre) {
        delete(DELETE_QUERY, genre.getId());
    }

    @Override
    public Map<Long, Genre> findAllAsMap() {
        return jdbcTemplate.query(FIND_ALL_QUERY, rs -> {
            HashMap<Long, Genre> map = new HashMap<>();
            while (rs.next()){
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                map.put(id, new Genre(id, name));
            }
            return map;
        });
    }
}
