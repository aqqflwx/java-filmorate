package ru.yandex.practicum.filmorate.storage.mpa;

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
@Import(MpaDbStorage.class)
public class MpaDbStorageTest {

    private final MpaDbStorage storage;

    @Test
    void testFindAll() {
        var all = storage.findAll();
        assertThat(all).hasSize(5);
        assertThat(all).extracting("id").containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void testGetById() {
        var m = storage.getById(1);
        assertThat(m).isPresent();
        assertThat(m.get().getName()).isEqualTo("G");
    }
}
