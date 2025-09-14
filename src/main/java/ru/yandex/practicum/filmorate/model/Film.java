package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    public static final int MAX_DESCRIPTION_LEN = 200;

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(
            max = MAX_DESCRIPTION_LEN,
            message = "Описание фильма не может быть больше 200 символов"
    )
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть > 0")
    private Integer duration;
}
