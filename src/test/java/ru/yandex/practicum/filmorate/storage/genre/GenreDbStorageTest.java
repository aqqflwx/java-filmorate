package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
public class GenreDbStorageTest {

    private final GenreDbStorage storage;

    @Test
    void testFindAll() {
        var all = storage.findAll();
        assertThat(all).hasSize(6);
        assertThat(all).extracting("id").containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    void testGetById() {
        var g = storage.getById(1);
        assertThat(g).isPresent();
        assertThat(g.get().getName()).isEqualTo("Комедия");
    }
}
