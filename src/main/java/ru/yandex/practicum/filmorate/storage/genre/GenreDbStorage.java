package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    private final RowMapper<Genre> mapper = (rs, rowNum) ->
            new Genre(rs.getInt("genre_id"), rs.getString("name"));

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT genre_id, name FROM genre ORDER BY genre_id", mapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        try {
            Genre g = jdbc.queryForObject("SELECT genre_id, name FROM genre WHERE genre_id=?", mapper, id);
            return Optional.ofNullable(g);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
