package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * REST-контроллер для операций с фильмами.
 */
@RestController
@RequestMapping("/films")
@Slf4j
public final class FilmController {

    /** Самая ранняя допустимая дата релиза. */
    private static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    /** Памятное хранилище фильмов. */
    private final Map<Long, Film> films = new HashMap<>();

    /**
     * Вернуть все фильмы.
     *
     * @return коллекция фильмов
     */
    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Обработка запроса на поиск всех фильмов выполнена");
        return films.values();
    }

    /**
     * Создать фильм.
     *
     * @param film тело запроса
     * @return сохранённый фильм
     */
    @PostMapping
    public Film create(@Valid @RequestBody final Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавление фильма было успешно выполнено");
        return film;
    }

    /**
     * Обновить фильм.
     *
     * @param newFilm фильм с обновлёнными данными
     * @return обновлённый фильм
     */
    @PutMapping
    public Film update(@Valid @RequestBody final Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Указан некорректный ID фильма для обновления");
            throw new ValidationException("ID фильма не указан!");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.warn("Введён несуществующий ID фильма");
            throw new ValidationException("Фильма с таким ID нет!");
        }
        validateFilm(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с ID: {} был обновлен", newFilm.getId());
        return newFilm;
    }

    /**
     * Проверка бизнес-правил фильма.
     * @param film фильм для проверки
     */
    private void validateFilm(final Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Пустое название фильма");
            throw new ValidationException(
                    "Название фильма не может быть пустым");
        }
        final String desc = film.getDescription();
        if (desc != null && desc.length() > Film.MAX_DESCRIPTION_LEN) {
            log.warn("Слишком длинное описание: {}", desc.length());
            throw new ValidationException(
                    "Описание фильма не может быть больше "
                            + Film.MAX_DESCRIPTION_LEN + " символов"
            );
        }
        if (film.getReleaseDate() != null
                && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.warn("Дата релиза раньше допустимой");
            throw new ValidationException(
                    "Дата релиза фильма должна быть не раньше 28.12.1895"
            );
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Неположительная длительность: {}", film.getDuration());
            throw new ValidationException(
                    "Продолжительность фильма должна "
                            + "быть положительным числом (> 0)"
            );
        }
    }

    /**
     * Сгенерировать следующий идентификатор.
     * @return следующий доступный идентификатор
     */
    private long getNextId() {
        final long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("Был сгенерирован новый ID для нового фильма");
        return currentMaxId + 1;
    }
}
