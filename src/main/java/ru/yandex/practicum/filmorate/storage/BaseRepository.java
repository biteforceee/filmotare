package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import static java.util.function.Predicate.not;

@Repository
@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> rowMapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, rowMapper, params);
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbcTemplate.update(query, id);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS
            );
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i+1, params[i]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected Set<Long> findIds(String query, Object... params) {
        return new HashSet<>(jdbcTemplate.query(query, (rs, rowNum) -> rs.getLong(1), params));
    }

    // Помогает пакетно сохранять коллекции (жадный/функциональный batch update)
    protected void batchUpdateIds(String deleteQuery, String insertQuery, Long entityId, Collection<Long> targetIds) {
        // 1. Сначала удаляем старые связи
        jdbcTemplate.update(deleteQuery, entityId);

        // 2. Если коллекция пустая, выходим
        if (targetIds == null || targetIds.isEmpty()) return;

        // 3. Пакетно вставляем новые связи одной командой к БД
        List<Object[]> batchArgs = targetIds.stream()
                .map(targetId -> new Object[]{entityId, targetId})
                .toList();

        jdbcTemplate.batchUpdate(insertQuery, batchArgs);
    }

    protected void syncRelatedIds(
            String findQuery,         // Запрос на получение текущих ID
            String insertQuery,       // Запрос на вставку
            String deleteSingleQuery, // Запрос на удаление конкретной связи
            Long entityId,            // ID фильма
            Collection<Long> newIds,  // Новые ID от клиента
            String idColumnName       // Имя колонки (genre_id или user_id)
    ) {
        Set<Long> currentIds = new HashSet<>(jdbcTemplate.query(findQuery, (rs, rowNum) -> rs.getLong(idColumnName), entityId));

        Set<Long> targetIds = Optional.ofNullable(newIds).map(HashSet::new).orElseGet(HashSet::new);

        List<Object[]> toInsert = targetIds.stream()
                .filter(id -> !currentIds.contains(id))
                .map(id -> new Object[]{entityId, id})
                .toList();

        List<Object[]> toDelete = currentIds.stream()
                .filter(id -> !targetIds.contains(id))
                .map(id -> new Object[]{entityId, id})
                .toList();

        Optional.of(toInsert).filter(not(List::isEmpty)).ifPresent(list -> jdbcTemplate.batchUpdate(insertQuery, list));
        Optional.of(toDelete).filter(not(List::isEmpty)).ifPresent(list -> jdbcTemplate.batchUpdate(deleteSingleQuery, list));
    }

}
