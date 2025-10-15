package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    private static final String SQL_SELECT_ALL_MPA =
            "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
    private static final String SQL_SELECT_MPA_BY_ID =
            "SELECT mpa_id, name FROM mpa WHERE mpa_id=?";

    private final RowMapper<Mpa> mapper = (rs, rowNum) ->
            new Mpa(rs.getInt("mpa_id"), rs.getString("name"));

    @Override
    public List<Mpa> findAll() {
        return jdbc.query(SQL_SELECT_ALL_MPA, mapper);
    }

    @Override
    public Optional<Mpa> getById(int id) {
        try {
            Mpa m = jdbc.queryForObject(SQL_SELECT_MPA_BY_ID, mapper, id);
            return Optional.ofNullable(m);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
