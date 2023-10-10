package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.zip.DataFormatException;

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
    void validateReleaseFilmDate1800() {
        assertThrows(ValidationException.class,() -> filmController.validate(film));
    }

    @Test
    void validateReleaseFilmDate1900() {
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        filmController.create(film);
        assertEquals(filmController.getAll().size(),1);
    }

    @Test
    void validateEmptyField() {
        film.setName("");
        film.setDuration(0);
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(1900,4,6));
        assertThrows(ValidationException.class,() -> filmController.validate(film));
    }

    @Test
    void validateDescriptionLength() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль." +
                " Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги," +
                " а именно 20 миллионов. о Куглов, который за время «своего отсутствия»," +
                " стал кандидатом Коломбани.");
        assertThrows(ValidationException.class, ()-> filmController.validate(film));
    }

    @Test
    void testPostOneFilm() {
        film.setReleaseDate(LocalDate.of(1900,4,6));
        filmController.create(film);
        assertEquals(1,filmController.getAll().size());
    }

    @Test
    void testUpdateFilm() {
        film.setReleaseDate(LocalDate.of(1900,4,6));
        filmController.create(film);
        Film secondFilm = Film.builder()
                .id(1)
                .name("nameFilm2")
                .description("descriptionFilm2")
                .duration(40)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        assertEquals(secondFilm, filmController.update(secondFilm));
    }

    @Test
    void testUpdateFilmNotAssigned(){
        film.setReleaseDate(LocalDate.of(1900,4,6));
        filmController.create(film);
        Film secondFilm = Film.builder()
                .id(99)
                .name("nameFilm2")
                .description("descriptionFilm2")
                .duration(40)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        assertThrows(DataNotFoundException.class, ()-> filmController.update(secondFilm));
    }
}