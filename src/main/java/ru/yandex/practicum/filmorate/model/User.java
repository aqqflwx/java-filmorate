package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель пользователя.
 */
@Data
public class User {

    /** Идентификатор пользователя. */
    private Long id;

    /** Электронная почта. */
    @NotBlank(message = "Почта пользователя не может быть пустой")
    @Email(message = "Введен некорректный email")
    private String email;

    /** Логин (без пробелов). */
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    /** Отображаемое имя. */
    private String name;

    /** Дата рождения (не в будущем). */
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
