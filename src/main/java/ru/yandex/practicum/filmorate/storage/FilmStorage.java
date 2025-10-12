package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Film create(final Film film);

    Film update(final Film newFilm);

    Collection<Film> findAllFilms();

    Film findFilmById(Long id);

    Map<Long, Film> getFilms();

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getMostPopular(int count);

    Set<Long> getLikes(long filmId);
}
