package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Getter
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        films.put(film.getId(), film);
        log.info("Добавление фильма было успешно выполнено");
        return film;
    }

    @Override
    public Film update(final Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Указан некорректный ID фильма для обновления");
            throw new ValidationException("ID фильма не указан!");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Введён несуществующий ID фильма");
            throw new NotFoundException("Фильма с таким ID нет!");
        }
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID: {} был обновлен", newFilm.getId());
        return newFilm;
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Обработка запроса на поиск всех фильмов выполнена");
        return films.values();
    }

    private long getNextId() {
        final long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("Был сгенерирован новый ID для нового фильма");
        return currentMaxId + 1;
    }

    @Override
    public Film findFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильма с таким ID не существует!");
        }
        return films.get(id);
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void deleteLike(long filmId, long userId) {

    }

    @Override
    public List<Film> getMostPopular(int count) {
        return List.of();
    }

    @Override
    public Set<Long> getLikes(long filmId) {
        return Set.of();
    }
}
