package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class FilmControllerTest {

    @Autowired
    FilmController filmController;
    @Autowired
    UserController userController;
    @Autowired
    InMemoryFilmStorage inMemoryFilmStorage;
    Film film;
    Film secondFilm;
    Film lastFilm;
    User user;
    User secondUser;
    String longDescription = "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль." +
            " Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги," +
            " а именно 20 миллионов. о Куглов, который за время «своего отсутствия»," +
            " стал кандидатом Коломбани.";

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("nameFilm")
                .description("descriptionFilm")
                .duration(40)
                .releaseDate(LocalDate.of(1800, 1, 1))
                .likes(new ArrayList<>())
                .build();

        secondFilm = Film.builder()
                .name("nameFilm1")
                .description("descriptionFilm1")
                .duration(50)
                .releaseDate(LocalDate.of(1920, 1, 1))
                .likes(new ArrayList<>())
                .build();

        lastFilm = Film.builder()
                .name("nameFilm2")
                .description("descriptionFilm2")
                .duration(70)
                .releaseDate(LocalDate.of(1940, 1, 1))
                .likes(new ArrayList<>())
                .build();

        user = User.builder()
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();

        secondUser = User.builder()
                .name("nameUser1")
                .email("fork1@yandex.ru")
                .login("fork1")
                .birthday(LocalDate.of(1998,8,12))
                .build();
        inMemoryFilmStorage.deleteAll();

    }

    @Test
    void validateReleaseFilmDate1800() {
        assertThrows(ValidationException.class,() -> inMemoryFilmStorage.validate(film));
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
        assertThrows(ValidationException.class,() -> inMemoryFilmStorage.validate(film));
    }

    @Test
    void validateDescriptionLength() {
        film.setDescription(longDescription);
        assertThrows(ValidationException.class, () -> inMemoryFilmStorage.validate(film));
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
        Film newFilm = Film.builder()
                .id(film.getId())
                .name("nameFilm2")
                .description("descriptionFilm2")
                .duration(40)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        assertEquals(newFilm, filmController.update(newFilm));
    }

    @Test
    void testUpdateFilmNotAssigned() {
        film.setReleaseDate(LocalDate.of(1900,4,6));
        filmController.create(film);
        Film newFilm = Film.builder()
                .id(99)
                .name("nameFilm2")
                .description("descriptionFilm2")
                .duration(40)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .build();
        assertThrows(DataNotFoundException.class, () -> filmController.update(newFilm));
    }

    @Test
    void testDeleteFilmById() {
        filmController.create(secondFilm);
        filmController.deleteById(secondFilm.getId());
        assertEquals(filmController.getAll().size(), 0);
    }

    @Test
    void failedDeleteById() {
        filmController.create(secondFilm);
        assertThrows(DataNotFoundException.class, () -> filmController.deleteById(99));
    }

    @Test
    void testGetAllFilmsForOneFilm() {
        filmController.create(secondFilm);
        assertEquals(filmController.getAll().size(), 1);
    }

    @Test
    void testGetAllFilmsForTwoFilm() {
        filmController.create(secondFilm);
        filmController.create(lastFilm);
        assertEquals(filmController.getAll().size(), 2);
    }

    @Test
    void testGetAllFilmsForNull() {
        assertEquals(filmController.getAll().size(), 0);
    }

    @Test
    void testGetByIdFilm() {
        filmController.create(secondFilm);
        assertEquals(filmController.getById(secondFilm.getId()), secondFilm);
    }

    @Test
    void failedGetByIdFilm() {
        filmController.create(secondFilm);
        assertThrows(DataNotFoundException.class, () -> filmController.getById(99));
    }

    @Test
    void testAddLikeForFilm() {
        filmController.create(secondFilm);
        userController.create(user);
        filmController.addLike(secondFilm.getId(), user.getId());
        assertEquals(secondFilm.getLikes().size(), 1);
    }

    @Test
    void failedAddLikeForFilm() {
        filmController.create(secondFilm);
        userController.create(user);
        assertThrows(DataNotFoundException.class, () -> filmController.addLike(99, user.getId()));
    }

    @Test
    void testDeleteLikeForFilm() {
        filmController.create(secondFilm);
        userController.create(user);
        filmController.addLike(secondFilm.getId(), user.getId());
        filmController.deleteLike(secondFilm.getId(), user.getId());
        assertEquals(secondFilm.getLikes().size(), 0);
    }

    @Test
    void failedDeleteLikeForFilm() {
        filmController.create(secondFilm);
        userController.create(user);
        filmController.addLike(secondFilm.getId(), user.getId());
        assertThrows(DataNotFoundException.class, () -> filmController.deleteLike(2,
                user.getId()));
    }

    @Test
    void testGetPopularFilm() {
        filmController.create(secondFilm);
        filmController.create(lastFilm);
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        filmController.create(film);
        userController.create(user);
        userController.create(secondUser);
        filmController.addLike(secondFilm.getId(), user.getId());
        filmController.addLike(secondFilm.getId(), secondUser.getId());
        filmController.addLike(lastFilm.getId(), user.getId());
        assertEquals(filmController.getPopular(3).get(0), secondFilm);
        assertEquals(filmController.getPopular(3).size(), 3);
    }
}