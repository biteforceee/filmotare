package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Long id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;

    public User(Long id, String login, String email, String name, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}
