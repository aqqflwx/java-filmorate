package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    User create(final User user);

    User update(final User newUser);

    Collection<User> findAllUsers();

    User findUserById(Long id);

    Map<Long, User> getUsers();
}
