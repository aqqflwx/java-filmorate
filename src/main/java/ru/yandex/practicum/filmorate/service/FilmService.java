package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    public Film create(Film film) {
        if (mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Ошибка ввода! MPA с таким ID не существует");
        }
        Set<Integer> allGenres = genreStorage.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Integer> filmGenres = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        if (!allGenres.containsAll(filmGenres)) {
            throw new NotFoundException("Ошибка ввода! Жанра с таким ID не существует");
        }
        return filmStorage.create(film);
    }

    public Film update(final Film newFilm) {
        if (!filmStorage.getFilms().containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }
        return filmStorage.update(newFilm);
    }

    public void addLike(Long id, Long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        filmStorage.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        if (!filmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует");
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Ошибка ввода! Пользователя с таким ID не существует");
        }

        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> topFilmsByLikes(Integer count) {

        if (count == null) {
            count = 10;
        }

        if (count <= 0) {
            throw new ValidationException("Параметр 'count' должен быть не меньше 1!");
        }

        return filmStorage.getMostPopular(count);
    }

}
