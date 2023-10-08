package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = Film.builder()
                .name("nameFilm")
                .description("descriptionFilm")
                .duration(40)
                .releaseDate(LocalDate.of(1800, 1, 1))
                .build();
    }

    @Test
    void falseValidate() {
        assertThrows(ValidationException.class,() -> filmController.validate(film));
    }

    @Test
    void trueValidate() {
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        filmController.create(film);
        assertEquals(filmController.getAll().size(),1);
    }

    @Test
    void nullValidate() {
        film.setName("");
        film.setDuration(0);
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(1900,4,6));
        assertThrows(ValidationException.class,() -> filmController.validate(film));
    }

}