package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    private final UserController controller = new UserController();
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
        controller.create(user1);
        List<User> users = controller.findAll().stream().toList();
        assertEquals(1, users.size());
        controller.create(user2);
        users = controller.findAll().stream().toList();
        assertEquals(2, users.size());
    }

    @Test
    void create() {
        User newUser = controller.create(user1);
        assertNotNull(newUser);
        assertEquals(user1, newUser);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badEmail));
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badLogin));
        assertThrows(ConditionsNotMetException.class, () -> controller.create(badBirthday));
        assertThrows(DuplicatedDataException.class, () -> controller.create(secondEmail));
    }

    @Test
    void update() {
        User newUser = controller.create(user1);
        assertEquals(user1, newUser);
        newUser.setEmail("123");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newUser));
        newUser.setEmail("user1-2@email.com");
        newUser.setBirthday(LocalDate.of(2222,1,1));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newUser));
        newUser.setBirthday(LocalDate.of(1999,12,23));
        newUser.setLogin("   login   ");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(newUser));
        newUser.setLogin("user1-2");
        newUser.setName(null);
        User user = controller.update(newUser);
        assertEquals(newUser, user);
        assertEquals(newUser.getLogin(), user.getLogin());
    }
}