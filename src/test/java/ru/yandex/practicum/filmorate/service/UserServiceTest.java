package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    private final UserService service = new UserService(new InMemoryUserStorage());
    User user1, user2, badEmail, badLogin, badBirthday, secondEmail, friendUser;

    @BeforeEach
    void setUp() {
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

        badEmail = new User(
                null,
                "user2",
                "123",
                "",
                LocalDate.of(2003, 2, 2)
        );

        badLogin = new User(
                null,
                "   ",
                "badLogin@email.com",
                "badLogin",
                LocalDate.of(2003, 2, 2)
        );

        badBirthday = new User(
                null,
                "badBirthday",
                "badBirthday@email.com",
                "",
                LocalDate.of(2222, 2, 2)
        );

        secondEmail = new User(
                null,
                "badName",
                "user1@email.com",
                "",
                LocalDate.of(2000, 2, 2)
        );

        friendUser = new User(
                null,
                "friend",
                "friend@email.com",
                "",
                LocalDate.of(2000, 2, 2)
        );
    }

    @Test
    void findAll() {
        service.create(user1);
        List<User> users = service.findAll().stream().toList();
        assertEquals(1, users.size());
        service.create(user2);
        users = service.findAll().stream().toList();
        assertEquals(2, users.size());
    }

    @Test
    void create() {
        User newUser = service.create(user1);
        assertNotNull(newUser);
        assertEquals(user1, newUser);
        assertThrows(ConditionsNotMetException.class, () -> service.create(badEmail));
        assertThrows(ConditionsNotMetException.class, () -> service.create(badLogin));
        assertThrows(ConditionsNotMetException.class, () -> service.create(badBirthday));
        assertThrows(DuplicatedDataException.class, () -> service.create(secondEmail));
    }

    @Test
    void update() {
        User newUser = service.create(user1);
        assertEquals(user1, newUser);
        newUser.setEmail("123");
        assertThrows(ConditionsNotMetException.class, () -> service.update(newUser));
        newUser.setEmail("user1-2@email.com");
        newUser.setBirthday(LocalDate.of(2222,1,1));
        assertThrows(ConditionsNotMetException.class, () -> service.update(newUser));
        newUser.setBirthday(LocalDate.of(1999,12,23));
        newUser.setLogin("   login   ");
        assertThrows(ConditionsNotMetException.class, () -> service.update(newUser));
        newUser.setLogin("user1-2");
        newUser.setName(null);
        User user = service.update(newUser);
        assertEquals(newUser, user);
        assertEquals(newUser.getLogin(), user.getLogin());
    }

    @Test
    void addFriend() {
        User newUser = service.create(user1);
        User friend = service.create(friendUser);

        int friendCount = 0;

        assertEquals(0, newUser.getFriends().size());

        service.addFriend(newUser.getId(), friend.getId());
        friendCount++;

        assertEquals(friendCount, newUser.getFriends().size());
        assertEquals(friendCount, friend.getFriends().size());
    }

    @Test
    void deleteFriend() {
        User newUser = service.create(user1);
        User friend = service.create(friendUser);

        assertEquals(0, newUser.getFriends().size());

        service.addFriend(newUser.getId(), friend.getId());

        assertEquals(1, newUser.getFriends().size());
        assertEquals(1, friend.getFriends().size());

        service.deleteFriend(newUser.getId(), friend.getId());

        assertEquals(0, newUser.getFriends().size());
        assertEquals(0, friend.getFriends().size());
    }

    @Test
    void getUserFriends() {
        User newUser = service.create(user1);
        User newUser2 = service.create(user2);
        User newFriendUser = service.create(friendUser);
        Set<User> friends = Set.of(newUser2, newFriendUser);

        service.addFriend(newUser.getId(), newUser2.getId());
        service.addFriend(newUser.getId(), newFriendUser.getId());

        assertEquals(friends.size(), newUser.getFriends().size());
        assertEquals(friends, service.getUserFriends(newUser.getId()));
    }

    @Test
    void getCommonFriends() {
        User newUser = service.create(user1);
        User newUser2 = service.create(user2);
        User newFriendUser = service.create(friendUser);
        Set<User> commonFriends = Set.of(newFriendUser);

        service.addFriend(newUser.getId(), newFriendUser.getId());
        service.addFriend(newUser2.getId(), newFriendUser.getId());

        assertEquals(commonFriends, service.getCommonFriends(newUser.getId(), newUser2.getId()));
    }
}