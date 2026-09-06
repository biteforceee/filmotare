package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.*;

@Repository("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(name, email, login, birthday)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * from friends";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT u.* \n" +
                                                                "FROM users u\n" +
                                                                "JOIN friends f ON u.id = f.friend_id\n" +
                                                                "WHERE f.user_id = ?;";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?) ON CONFLICT (user_id, friend_id) DO NOTHING";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM likes WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<User> findAll() {
        List<User> users = findMany(FIND_ALL_QUERY);

        Map<Long, Set<Long>> friends = jdbcTemplate.query(FIND_ALL_FRIENDS_QUERY, rs -> {
            HashMap<Long, Set<Long>> map = new HashMap<>();
            while (rs.next()){
                Long userId = rs.getLong("user_id");
                Long friendId = rs.getLong("friend_id");
                map.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
            }
            return map;
        });

        users.forEach(user -> user.setFriends(friends.get(user.getId())));

        return users;
    }

    @Override
    public User getById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("пользователь с id: " + id + " не найден"));
    }

    @Override
    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );

        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public void delete(User user) {
        delete(DELETE_QUERY, user.getId());
    }

    public List<User> findUserFriends(Long userId) {
        return findMany(FIND_FRIENDS_BY_USER_ID_QUERY, userId);
    }
}
