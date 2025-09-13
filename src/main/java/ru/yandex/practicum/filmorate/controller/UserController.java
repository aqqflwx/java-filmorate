package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * REST-контроллер для операций с пользователями.
 */
@RestController
@RequestMapping("/users")
@Slf4j
public final class UserController {

    /** Памятное хранилище пользователей. */
    private final Map<Long, User> users = new HashMap<>();

    /**
     * Вернуть всех пользователей.
     *
     * @return коллекция пользователей
     */
    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Обработка запроса на поиск всех пользователей выполнена");
        return users.values();
    }

    /**
     * Создать пользователя.
     *
     * @param user тело запроса
     * @return сохранённый пользователь
     */
    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавление пользователя было успешно выполнено");
        return user;
    }

    /**
     * Обновить пользователя.
     *
     * @param newUser пользователь с обновлёнными данными
     * @return обновлённый пользователь
     */
    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
        if (newUser.getId() == null) {
            log.warn("Указан некорректный ID пользователя для обновления");
            throw new ValidationException("ID пользователя не указан!");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Введён несуществующий ID пользователя");
            throw new ValidationException("Пользователя с таким ID нет!");
        }
        validateUser(newUser);
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID: {} был обновлен", newUser.getId());
        return newUser;
    }

    /**
     * Проверка бизнес-правил пользователя.
     * @param user пользователь для проверки
     */
    private void validateUser(final User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Пустое поле email");
            throw new ValidationException("Email"
                    + " пользователя не должен быть пустым!");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Некорректный email — нет '@'");
            throw new ValidationException("Email должен содержать символ '@'!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Отсутствует логин пользователя");
            throw new ValidationException("Логин пользователя отсутствует!");
        }
        if (user.getLogin().chars().anyMatch(Character::isWhitespace)) {
            log.warn("Логин содержит пробельные символы");
            throw new ValidationException("Логин не должен содержать пробелы!");
        }
        if (user.getBirthday() != null
                && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем");
            throw new ValidationException("Дата "
                    + "рождения не может быть в будущем!");
        }
    }

    /**
     * Сгенерировать следующий идентификатор.
     * @return следующий доступный идентификатор
     */
    private long getNextId() {
        final long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("Был сгенерирован новый ID для пользователя");
        return currentMaxId + 1;
    }
}
