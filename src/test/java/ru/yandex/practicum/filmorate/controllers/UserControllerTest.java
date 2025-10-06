package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        controller  = new UserController(userStorage, userService);
    }

    private User validUser() {
        User u = new User();
        u.setEmail("a@b.c");
        u.setLogin("login");
        u.setName("Name");
        u.setBirthday(LocalDate.of(2000, 1, 1));
        return u;
    }

    @Test
    void create_ok_whenAllValid() {
        assertDoesNotThrow(() -> controller.create(validUser()));
        assertEquals(1, controller.findAllUsers().size());
    }

    @Test
    void create_rejects_emptyEmail() {
        User u = validUser();
        u.setEmail("");
        assertThrows(ValidationException.class, () -> controller.create(u));
    }

    @Test
    void create_rejects_emailWithoutAt() {
        User u = validUser();
        u.setEmail("wrong.email");
        assertThrows(ValidationException.class, () -> controller.create(u));
    }

    @Test
    void create_rejects_blankLogin() {
        User u = validUser();
        u.setLogin("   ");
        assertThrows(ValidationException.class, () -> controller.create(u));
    }

    @Test
    void create_rejects_loginWithSpacesInside() {
        User u = validUser();
        u.setLogin("lo gin");
        assertThrows(ValidationException.class, () -> controller.create(u));
    }

    @Test
    void create_setsNameFromLogin_whenNameIsBlank() {
        User u = validUser();
        u.setName("   ");
        User saved = controller.create(u);
        assertEquals(u.getLogin(), saved.getName());
    }

    @Test
    void create_rejects_futureBirthday() {
        User u = validUser();
        u.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> controller.create(u));
    }
}
