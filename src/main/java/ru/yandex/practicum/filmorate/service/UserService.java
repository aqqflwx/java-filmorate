package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public void addFriend(Long id, Long friendId) {

        if (id == null || friendId == null) {
            throw new ValidationException("Проверьте корректность ввода");
        }

        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        }

        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Пользователя с ID " + friendId + " не существует");
        }

        userStorage.findUserById(id).getFriends().add(friendId);
        userStorage.findUserById(friendId).getFriends().add(id);
    }

    public void removeFriend(Long id, Long friendId) {
        if (!userStorage.getUsers().containsKey(id)
                || !userStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Проверьте корректность ввода! Пользователя не существует");
        }

        userStorage.findUserById(id).getFriends().remove(friendId);
        userStorage.findUserById(friendId).getFriends().remove(id);
    }

    public Collection<User> listFriends(Long id) {
        if (id == null) {
            throw new ValidationException("Ввёден некорректный ID!");
        }

        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Этого пользователя не существует!");
        }

        Set<Long> listIdFriends = userStorage.findUserById(id).getFriends();

        return userStorage.getUsers().values().stream()
                .filter(user -> listIdFriends.contains(user.getId()))
                .toList();
    }

    public Collection<User> listMutualFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            throw new ValidationException("Ввёден некорректный ID!");
        }

        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        }

        if (!userStorage.getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователя с ID " + otherId + " не существует");
        }

        Set<Long> listIdFriends = userStorage.findUserById(id).getFriends();
        Set<Long> listOtherIdFriends = userStorage.findUserById(otherId).getFriends();

        List<Long> mutualFriends = listIdFriends.stream()
                .filter(listOtherIdFriends::contains)
                .toList();

        return userStorage.getUsers().values().stream()
                .filter(user -> mutualFriends.contains(user.getId()))
                .toList();
    }

}
