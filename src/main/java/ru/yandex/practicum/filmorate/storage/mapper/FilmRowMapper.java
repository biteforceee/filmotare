package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());
        film.setDuration(rs.getLong("duration"));
        film.setRatingId(rs.getLong("mpa_rating"));
        film.setUsersLikes(new HashSet<>());

        return film;
    }
}
