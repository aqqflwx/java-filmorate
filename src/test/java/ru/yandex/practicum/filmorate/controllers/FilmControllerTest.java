package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
    }

    private Film validFilm() {
        Film f = new Film();
        f.setName("Interstellar");
        f.setDescription("good film");
        f.setReleaseDate(LocalDate.of(1895, 12, 28));
        f.setDuration(120);
        return f;
    }

    @Test
    void create_ok_whenAllValid() {
        Film saved = controller.create(validFilm());
        assertNotNull(saved.getId());
        assertEquals("Interstellar", saved.getName());
        assertEquals(1, controller.findAllFilms().size());
    }

    @Test
    void create_rejects_blankName() {
        Film f = validFilm();
        f.setName("   ");
        assertThrows(ValidationException.class, () -> controller.create(f));
    }

    @Test
    void create_rejects_descriptionLongerThan200() {
        Film f = validFilm();
        f.setDescription("a".repeat(201));
        assertThrows(ValidationException.class, () -> controller.create(f));
    }

    @Test
    void create_allows_descriptionExactly200() {
        Film f = validFilm();
        f.setDescription("a".repeat(200));
        assertDoesNotThrow(() -> controller.create(f));
    }

    @Test
    void create_rejects_releaseDateBefore_1895_12_28() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> controller.create(f));
    }

    @Test
    void create_allows_releaseDateExactly_1895_12_28() {
        Film f = validFilm();
        f.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> controller.create(f));
    }

    @Test
    void create_rejects_zeroOrNegativeDuration() {
        Film f = validFilm();
        f.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.create(f));
        f.setDuration(-1);
        assertThrows(ValidationException.class, () -> controller.create(f));
    }
}
