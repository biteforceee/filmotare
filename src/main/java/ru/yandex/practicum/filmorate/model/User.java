package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
public class User {
    Long id;
    String login;
    String email;
    String name;
    LocalDate birthday;
}
