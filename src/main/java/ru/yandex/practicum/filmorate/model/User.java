package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "email")
@AllArgsConstructor
public class User {
    private Long id;
    private String login;
    private String email;
    private String name;
    private LocalDate birthday;
}
