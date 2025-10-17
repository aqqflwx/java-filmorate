package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage storage;

    public List<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa getById(int id) {
        return storage.getById(id).orElseThrow(() ->
                new NotFoundException("Ошибка ввода! MPA с таким ID не существует"));
    }
}
