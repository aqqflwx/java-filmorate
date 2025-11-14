package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        final long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("Был сгенерирован новый ID для пользователя");
        return currentMaxId + 1;
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (user.getFriendRequests() == null) {
            user.setFriendRequests(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("Добавление пользователя было успешно выполнено");
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Указан некорректный ID пользователя для обновления");
            throw new ValidationException("ID пользователя не указан!");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Введён несуществующий ID пользователя");
            throw new NotFoundException("Пользователя с таким ID нет!");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getFriends() == null) {
            newUser.setFriends(new HashSet<>());
        }
        if (newUser.getFriendRequests() == null) {
            newUser.setFriendRequests(new HashSet<>());
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID: {} был обновлен", newUser.getId());
        return newUser;
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Обработка запроса на поиск всех пользователей выполнена");
        return users.values();
    }

    @Override
    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с таким ID не существует!");
        }
        return users.get(id);
    }
}
