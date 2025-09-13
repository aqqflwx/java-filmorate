package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Модель фильма.
 */
@Data
public class Film {

    /** Максимальная допустимая длина описания. */
    public static final int MAX_DESCRIPTION_LEN = 200;

    /** Идентификатор фильма. */
    private Long id;

    /** Название фильма (обязательно). */
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /** Краткое описание (не более 200 символов). */
    @Size(
            max = MAX_DESCRIPTION_LEN,
            message = "Описание фильма не может быть больше 200 символов"
    )
    private String description;

    /** Дата релиза (не раньше 28.12.1895). */
    private LocalDate releaseDate;

    /** Длительность (минуты, строго > 0). */
    @Positive(message = "Продолжительность фильма должна быть > 0")
    private Integer duration;
}
