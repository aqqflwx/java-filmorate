package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_MPA_BY_ID =
            "SELECT mpa_id, name FROM mpa WHERE mpa_id=?";
    private static final String SQL_SELECT_GENRES_BY_FILM_ID =
            "SELECT g.genre_id, g.name FROM film_genre fg JOIN genre g ON g.genre_id=fg.genre_id WHERE fg.film_id=? ORDER BY g.genre_id";
    private static final String SQL_SELECT_ALL_FILMS =
            "SELECT film_id, name, description, release_date, duration, mpa_id FROM films ORDER BY film_id";
    private static final String SQL_SELECT_FILM_BY_ID =
            "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id=?";
    private static final String SQL_INSERT_FILM =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE_FILM =
            "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?";
    private static final String SQL_DELETE_FILM_GENRES_BY_FILM_ID =
            "DELETE FROM film_genre WHERE film_id=?";
    private static final String SQL_INSERT_FILM_GENRE =
            "INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)";
    private static final String SQL_MERGE_FILM_LIKE =
            "MERGE INTO film_likes(film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
    private static final String SQL_DELETE_FILM_LIKE =
            "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
    private static final String SQL_SELECT_MOST_POPULAR =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                    "FROM films f LEFT JOIN film_likes l ON f.film_id=l.film_id " +
                    "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                    "ORDER BY COUNT(l.user_id) DESC, f.film_id " +
                    "LIMIT ?";
    private static final String SQL_SELECT_LIKES_BY_FILM_ID =
            "SELECT user_id FROM film_likes WHERE film_id=?";

    private Mpa loadMpa(Integer id) {
        if (id == null) return null;
        try {
            Map<String, Object> row = jdbc.queryForMap(SQL_SELECT_MPA_BY_ID, id);
            return new Mpa(((Number) row.get("mpa_id")).intValue(), (String) row.get("name"));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Set<Genre> loadGenres(long filmId) {
        List<Map<String, Object>> rows = jdbc.queryForList(SQL_SELECT_GENRES_BY_FILM_ID, filmId);
        Set<Genre> res = new LinkedHashSet<>();
        for (Map<String, Object> r : rows) {
            res.add(new Genre(((Number) r.get("genre_id")).intValue(), (String) r.get("name")));
        }
        return res;
    }

    private Film mapFilm(Map<String, Object> row) {
        Film f = new Film();
        f.setId(((Number) row.get("film_id")).longValue());
        f.setName((String) row.get("name"));
        f.setDescription((String) row.get("description"));
        Date rd = (Date) row.get("release_date");
        if (rd != null) {
            f.setReleaseDate(rd.toLocalDate());
        }
        f.setDuration(((Number) row.get("duration")).intValue());
        Integer mpaId = row.get("mpa_id") == null ? null : ((Number) row.get("mpa_id")).intValue();
        f.setMpa(loadMpa(mpaId));
        f.setGenres(loadGenres(f.getId()));
        return f;
    }

    @Override
    public Collection<Film> findAllFilms() {
        List<Map<String, Object>> rows = jdbc.queryForList(SQL_SELECT_ALL_FILMS);
        List<Film> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(mapFilm(r));
        }
        return result;
    }

    @Override
    public Film findFilmById(Long id) {
        try {
            Map<String, Object> row = jdbc.queryForMap(SQL_SELECT_FILM_BY_ID, id);
            return mapFilm(row);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Map<Long, Film> getFilms() {
        return findAllFilms().stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
    }

    @Override
    public Film create(Film film) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_FILM, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() == null ? null : Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() == null ? null : film.getMpa().getId());
            return ps;
        }, kh);
        Number key = Objects.requireNonNull(kh.getKey());
        film.setId(key.longValue());
        replaceGenres(film.getId(), film.getGenres());
        return findFilmById(film.getId());
    }

    @Override
    public Film update(Film film) {
        jdbc.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() == null ? null : Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() == null ? null : film.getMpa().getId(),
                film.getId());
        replaceGenres(film.getId(), film.getGenres());
        return findFilmById(film.getId());
    }

    private void replaceGenres(long filmId, Set<Genre> genres) {
        jdbc.update(SQL_DELETE_FILM_GENRES_BY_FILM_ID, filmId);
        if (genres == null || genres.isEmpty()) return;
        List<Genre> list = genres.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Genre::getId, g -> g, (a, b) -> a, TreeMap::new))
                .values().stream().distinct().toList();
        jdbc.batchUpdate(SQL_INSERT_FILM_GENRE, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, list.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(SQL_MERGE_FILM_LIKE, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(SQL_DELETE_FILM_LIKE, filmId, userId);
    }

    public List<Film> getMostPopular(int count) {
        List<Map<String, Object>> rows = jdbc.queryForList(SQL_SELECT_MOST_POPULAR, count);
        List<Film> res = new ArrayList<>();
        for (Map<String, Object> r : rows) res.add(mapFilm(r));
        return res;
    }

    public Set<Long> getLikes(long filmId) {
        return new LinkedHashSet<>(jdbc.query(SQL_SELECT_LIKES_BY_FILM_ID, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }
}
