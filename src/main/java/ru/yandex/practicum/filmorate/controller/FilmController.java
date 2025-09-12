package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Обработка запроса на поиск всех фильмов выполнена");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавление фильма было успешно выполнено");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Указан некорректный ID фильма для обновления");
            throw new ValidationException("ID фильма не указан!");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Ввёден несуществующий ID фильма");
            throw new ValidationException("Фильма с таким ID нет!");
        }

        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID: {} был обновлен", newFilm.getId());
        return newFilm;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Было введено пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым!");
        }

        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.warn("Было введено описание, состоящее из 200+ символов");
            throw new ValidationException("Максимальная длина описания фильма 200 символов!");
        }

        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.warn("Была введена невалидная дата (ранее дня рождения фильма)");
            throw new ValidationException("Дата релиза фильма должна быть позже 28.12.1895!");
        }

        if (film.getDuration() < 0) {
            log.warn("Была введена неположительная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}