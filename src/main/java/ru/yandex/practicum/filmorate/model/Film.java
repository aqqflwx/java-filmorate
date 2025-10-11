package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {

    public static final int MAX_DESCRIPTION_LEN = 200;
    public static final LocalDate EARLIEST_RELEASE_DATE = LocalDate
            .of(1895, 12, 28);

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

    private Set<Genre> genres;
    private Mpa mpa;

    private Set<Long> likes;

}
