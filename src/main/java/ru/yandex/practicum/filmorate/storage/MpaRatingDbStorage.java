package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage extends BaseRepository<MpaRating> implements MpaRatingStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO mpa_ratings(name) VALUES (?) returning id";
    private static final String UPDATE_QUERY = "UPDATE mpa_ratings SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM mpa_ratings WHERE id = ?";

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<MpaRating> getById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public MpaRating create(MpaRating mpaRating) {
        long id = insert(INSERT_QUERY, mpaRating.getName());
        mpaRating.setId(id);

        return mpaRating;
    }

    @Override
    public MpaRating update(MpaRating mpaRating) {
        update(UPDATE_QUERY, mpaRating.getName(), mpaRating.getId());

        return mpaRating;
    }

    @Override
    public void delete(MpaRating mpaRating) {
        delete(DELETE_QUERY, mpaRating.getId());
    }

    @Override
    public Map<Long, MpaRating> findAllAsMap() {
        return jdbcTemplate.query(FIND_ALL_QUERY, rs -> {
            HashMap<Long, MpaRating> map = new HashMap<>();
            while (rs.next()){
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                map.put(id, new MpaRating(id, name));
            }
            return map;
        });
    }
}
