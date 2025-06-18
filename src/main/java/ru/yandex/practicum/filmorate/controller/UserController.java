package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.trace("Получили всех пользователей.");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.trace("Создаем нового пользователя.");
        // проверяем выполнение необходимых условий
        log.debug("Валидация пользователя.");
        validate(user);
        for(Map.Entry<Long,User> e : users.entrySet()){
            if(e.getValue().getEmail().equals(user.getEmail())){
                log.warn("Этот имейл нельзя использовать при создании нового пользователя.");
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }
        // формируем дополнительные данные
        log.debug("Устанавливаем значение id у пользователя.");
        user.setId(getNextId());
        // сохраняем нового юзера в памяти приложения
        users.put(user.getId(), user);
        log.trace("Создали нового пользователя с id = {}.", user.getId());
        return user;
    }

    private void validate(User user){
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка с электронной почтой.");
            throw new ConditionsNotMetException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка с логином.");
            throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка с датой рождения.");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Заменили имя пользователя на значение логина.");
            user.setName(user.getLogin());
        }
    }

    // вспомогательный метод для генерации идентификатора нового юзера
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        log.trace("Обновляем данные пользователя.");
        if (newUser.getId() == null) {
            log.warn("Id должен быть указан.");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.debug("Валидация пользователя.");
            validate(newUser);
            if(!Objects.equals(oldUser.getEmail(), newUser.getEmail())){
                for(Map.Entry<Long,User> e : users.entrySet()){
                    if(e.getValue().getEmail().equals(newUser.getEmail())){
                        log.warn("Этот имейл уже используется.");
                        throw new DuplicatedDataException("Этот имейл уже используется");
                    }
                }
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            if (newUser.getEmail() != null) {
                log.debug("Обновили имейл пользователя.");
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getName() != null) {
                log.debug("Обновили имя пользователя.");
                oldUser.setName(newUser.getName());
            }
            if (newUser.getLogin() != null) {
                log.debug("Обновили логин пользователя.");
                oldUser.setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null) {
                log.debug("Обновили дату рождения пользователя.");
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.trace("Данные пользователя обновлены.");
            return oldUser;
        }
        log.warn("Юзер с id = {} не найден.", newUser.getId());
        throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
    }
}
