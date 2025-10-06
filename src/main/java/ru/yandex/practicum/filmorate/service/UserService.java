package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public void addFriend(Long id, Long friendId) {

        if (id == null || friendId == null) {
            throw new ValidationException("Проверьте корректность ввода");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Пользователя с ID " + friendId + " не существует");
        }

        inMemoryUserStorage.findUserById(id).getFriends().add(friendId);
        inMemoryUserStorage.findUserById(friendId).getFriends().add(id);
    }

    public void removeFriend(Long id, Long friendId) {
        if (!inMemoryUserStorage.getUsers().containsKey(id)
                || !inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Проверьте корректность ввода! Пользователя не существует");
        }

        inMemoryUserStorage.findUserById(id).getFriends().remove(friendId);
        inMemoryUserStorage.findUserById(friendId).getFriends().remove(id);
    }

    public Collection<User> listFriends(Long id) {
        if (id == null) {
            throw new ValidationException("Ввёден некорректный ID!");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Этого пользователя не существует!");
        }

        Set<Long> listIdFriends = inMemoryUserStorage.findUserById(id).getFriends();

        return inMemoryUserStorage.getUsers().values().stream()
                .filter(user -> listIdFriends.contains(user.getId()))
                .toList();
    }

    public Collection<User> listMutualFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            throw new ValidationException("Ввёден некорректный ID!");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователя с ID " + otherId + " не существует");
        }

        Set<Long> listIdFriends = inMemoryUserStorage.findUserById(id).getFriends();
        Set<Long> listOtherIdFriends = inMemoryUserStorage.findUserById(otherId).getFriends();

        List<Long> mutualFriends = listIdFriends.stream()
                .filter(listOtherIdFriends::contains)
                .toList();

        return inMemoryUserStorage.getUsers().values().stream()
                .filter(user -> mutualFriends.contains(user.getId()))
                .toList();
    }





}
