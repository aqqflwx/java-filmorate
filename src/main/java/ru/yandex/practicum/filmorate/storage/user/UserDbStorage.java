package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    private User mapUser(Map<String, Object> row) {
        User u = new User();
        u.setId(((Number) row.get("user_id")).longValue());
        u.setEmail((String) row.get("email"));
        u.setLogin((String) row.get("login"));
        u.setName((String) row.get("name"));
        Date bd = (Date) row.get("birthday");
        if (bd != null) {
            u.setBirthday(bd.toLocalDate());
        }
        return u;
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT user_id, email, login, name, birthday FROM users ORDER BY user_id";
        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        List<User> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(mapUser(r));
        }
        return result;
    }

    @Override
    public User findUserById(Long id) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id=?";
        try {
            Map<String, Object> row = jdbc.queryForMap(sql, id);
            return mapUser(row);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        return findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            if (user.getBirthday() != null) {
                ps.setDate(4, Date.valueOf(user.getBirthday()));
            } else {
                ps.setDate(4, null);
            }
            return ps;
        }, kh);
        Number key = Objects.requireNonNull(kh.getKey());
        user.setId(key.longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() == null ? null : Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    // ----- One-sided friendship (friend requests) -----
    public void addFriend(long userId, long friendId) {
        String sql = "MERGE INTO friendship(user_id, friend_id) KEY(user_id, friend_id) VALUES (?, ?)";
        jdbc.update(sql, userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        jdbc.update("DELETE FROM friendship WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM friendship f JOIN users u ON u.user_id=f.friend_id WHERE f.user_id=? ORDER BY u.user_id";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, userId);
        List<User> result = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            result.add(mapUser(r));
        }
        return result;
    }
}
