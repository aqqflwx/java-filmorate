package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Film create(final Film film);

    Film update(final Film newFilm);

    Collection<Film> findAllFilms();

    Film findFilmById(Long id);

    Map<Long, Film> getFilms();
}
