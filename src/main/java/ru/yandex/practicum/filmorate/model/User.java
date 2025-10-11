package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Почта пользователя не может быть пустой")
    @Email(message = "Введен некорректный email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    private LocalDate birthday;

    private Set<Long> friends;
    private Set<Long> friendRequests;

}
