package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<User>> findAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User newUser) {
        return new ResponseEntity<>(userService.update(newUser), HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<User>> getUserFriends(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserFriends(id), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<User>>  getUserFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return new ResponseEntity<>(userService.getCommonFriends(id, otherId), HttpStatusCode.valueOf(200));
    }
}
