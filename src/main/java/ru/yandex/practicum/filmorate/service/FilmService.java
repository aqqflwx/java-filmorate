package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(final Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void addLike(Long id, Long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        filmStorage.findFilmById(id).getLikes().add(userId);
    }

    public void removeLike(Long id, Long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        filmStorage.findFilmById(id).getLikes().remove(userId);
    }

    public Collection<Film> topFilmsByLikes(Integer count) {

        if (count == null) {
            count = 10;
        }

        if (count <= 0) {
            throw new ValidationException("Параметр 'count' должен быть не меньше 1!");
        }

        return filmStorage.getFilms().values().stream()
                .sorted(Comparator.<Film>comparingInt(
                        f -> f.getLikes() == null ? 0 : f.getLikes().size()
                ).reversed())
                .limit(count)
                .toList();
    }

}
