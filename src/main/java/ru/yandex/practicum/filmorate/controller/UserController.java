package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public final class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        validateUser(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
        validateUser(newUser);
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> listFriends(@PathVariable Long id) {
        return userService.listFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> listMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.listMutualFriends(id, otherId);
    }

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

}
