package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос на вывод фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на создание фильма");
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        log.info("Создание фильма - успех");
        film.setId(nextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление данных фильма");
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Введен неверный Id");
        } else if (film.getName() == null || film.getDescription() == null) {
            return film;
        }

        log.info("Обновление фильма - успех");
        films.put(film.getId(), film);
        return film;
    }

    public long nextId() {
        long currentId = films.values()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);

        return ++currentId;
    }

}
