package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;


/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
    Long id;

    String name;

    String description;

    LocalDate releaseDate;

    long duration;
}
