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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    private final UserService service = new UserService(new InMemoryUserStorage());
    User user1, user2, badEmail, badLogin, badBirthday, secondEmail;

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
}