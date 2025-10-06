package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public final class FilmController {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findUserById(@PathVariable(required = false) Long id) {
        return inMemoryFilmStorage.findFilmById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody final Film film) {
        validateFilm(film);
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody final Film newFilm) {
        validateFilm(newFilm);
        return inMemoryFilmStorage.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> topFilmsByLikes(@RequestParam(required = false) Integer count) {
        return filmService.topFilmsByLikes(count);
    }

    private void validateFilm(final Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Пустое название фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        final String desc = film.getDescription();
        if (desc != null && desc.length() > Film.MAX_DESCRIPTION_LEN) {
            log.warn("Слишком длинное описание: {}", desc.length());
            throw new ValidationException("Описание фильма не может быть больше " + Film.MAX_DESCRIPTION_LEN + " символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(Film.EARLIEST_RELEASE_DATE)) {
            log.warn("Дата релиза раньше допустимой");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28.12.1895");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Неположительная длительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна " + "быть положительным числом (> 0)");
        }
    }
}
