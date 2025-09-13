package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос на вывод пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный ввод электроной почты");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        log.info("Создание пользователя - успех");
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Получен запрос на обновление данных пользователя");
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Введен неверный Id");
        } else if (user.getName() == null || user.getEmail() == null || user.getLogin() == null) {
            return user;
        }

        log.info("Обновление пользователя - успех");
        users.put(user.getId(), user);
        return user;
    }

    public long nextId() {
        long currentId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);

        return ++currentId;
    }
}
