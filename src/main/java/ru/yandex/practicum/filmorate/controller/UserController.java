package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Обработка запроса на поиск всех пользователей выполнена");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавление пользователя было успешно выполнено");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Указан некорректный ID пользователя для обновления");
            throw new ValidationException("ID пользователя не указан!");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Ввёден несуществующий ID пользователя");
            throw new ValidationException("Пользователя с таким ID нет!");
        }

        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID: {} был обновлен", newUser.getId());
        return newUser;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Было введено пустое поле email");
            throw new ValidationException("Email пользователя не должен быть пустым!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Был получен некорректный email - отсутствие @");
            throw new ValidationException("Email должен содержать символ @!");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("В request отсутствует логин пользователя");
            throw new ValidationException("Логин пользователя отсутствует!");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Был введен некорректный логин пользователя: содержание пробелов");
            throw new ValidationException("Логин не должен содержать пробелы!");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Была получена дата, которая еще не наступила на текущий момент");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("Был сгенерирован новый ID для пользователя");
        return ++currentMaxId;
    }
}
