package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public void addLike(Long id, Long userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        inMemoryFilmStorage.findFilmById(id).getLikes().add(userId);
    }

    public void removeLike(Long id, Long userId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        inMemoryFilmStorage.findFilmById(id).getLikes().remove(userId);
    }

    public Collection<Film> topFilmsByLikes(Integer count) {

        if (count == null) {
            count = 10;
        }

        if (count <= 0) {
            throw new ValidationException("Параметр 'count' должен быть не меньше 1!");
        }

        return inMemoryFilmStorage.getFilms().values().stream()
                .sorted(Comparator.<Film>comparingInt(
                        f -> f.getLikes() == null ? 0 : f.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }
}
