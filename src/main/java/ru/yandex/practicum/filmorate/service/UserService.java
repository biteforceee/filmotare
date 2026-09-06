package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(Long id, Long friendId) {
        userStorage.getById(id).getFriends().add(friendId);
        userStorage.getById(friendId).getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        userStorage.getById(id).getFriends().remove(friendId);
        userStorage.getById(friendId).getFriends().remove(id);
    }

    public Set<User> getUserFriends(Long id) {
        return userStorage.getById(id).getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> userFriendsIds = userStorage.getById(id).getFriends();
        Set<Long> otherFriendsIds = userStorage.getById(otherId).getFriends();
        Set<User> otherFriends = userFriendsIds.stream()
                .filter(otherFriendsIds::contains)
                .map(userStorage::getById)
                .collect(Collectors.toSet());
        return otherFriends;
    }
}
