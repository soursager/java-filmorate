package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private  GenreDbStorage genreDbStorage;
    private  MpaDbStorage mpaDbStorage;

    private  FilmDbStorage filmStorage;
    private  UserDbStorage userDbStorage;
    private Film lastFilm;
    private Film secondFilm;
    private User firstUser;

    Mpa mpa = Mpa.builder()
            .id(1)
            .name("G")
            .build();

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate,mpaDbStorage,genreDbStorage);

        lastFilm = Film.builder()
                .name("nameFilm1")
                .description("descriptionFilm2")
                .duration(70)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1940, 1, 1))
                .likes(new ArrayList<>())
                .build();

        secondFilm = Film.builder()
                .name("nameFilm2")
                .description("descriptionFilm2")
                .mpa(mpa)
                .duration(70)
                .releaseDate(LocalDate.of(1940, 1, 1))
                .likes(new ArrayList<>())
                .build();

        firstUser = User.builder()
                .name("first")
                .email("first@yandex.ru")
                .login("first")
                .birthday(LocalDate.of(2005,8,12))
                .build();
    }

    @Test
    void testCreateFilm() {
        filmStorage.create(lastFilm);
        assertEquals(filmStorage.getAll().size(), 1);
    }

    @Test
    void updateFilmTest() {
        Film film = Film.builder()
                .name("name1")
                .description("descriptionFilm1")
                .duration(25)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1910, 1, 1))
                .likes(new ArrayList<>())
                .build();
        filmStorage.create(film);
        Film nextFilm= Film.builder()
                .id(1)
                .name("name2")
                .description("descriptionFilm2")
                .duration(30)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1967, 5, 25))
                .likes(new ArrayList<>())
                .build();
        filmStorage.update(nextFilm);
        assertEquals(filmStorage.getById(1).getName(), "name2", "Не сходится имя");
        assertEquals(filmStorage.getById(1).getDuration(), 30, "Не сходится продолжительность");
    }

    @Test
    void getAllTest() {
        Film film = Film.builder()
                .name("name1")
                .description("descriptionFilm1")
                .duration(25)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1910, 1, 1))
                .likes(new ArrayList<>())
                .build();
        Film nextFilm= Film.builder()
                .name("name2")
                .description("descriptionFilm2")
                .duration(30)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1967, 5, 25))
                .likes(new ArrayList<>())
                .build();
        filmStorage.create(film);
        filmStorage.create(nextFilm);
        List<Film> filmList = filmStorage.getAll();
        assertEquals(2, filmList.size(), "неверное количество фильмов");
    }

    @Test
    void getFilmByIdTest() {
        Film film = Film.builder()
                .name("name1")
                .description("descriptionFilm1")
                .duration(25)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1910, 1, 1))
                .likes(new ArrayList<>())
                .build();
        Film nextFilm= Film.builder()
                .name("name2")
                .description("descriptionFilm2")
                .duration(30)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1967, 5, 25))
                .likes(new ArrayList<>())
                .build();
        filmStorage.create(film);
        filmStorage.create(nextFilm);
        Film getFilm = filmStorage.getById(2);
        assertEquals(2, getFilm.getId(), "фильм вернулся неверный");
        assertThat(getFilm).hasFieldOrPropertyWithValue("name", "name2");
    }

     @Test
    void addLikeTest() {
        User user = User.builder()
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
         User nextUser = User.builder()
                 .name("nameUser1")
                 .email("fork1@yandex.ru")
                 .login("fork1")
                 .birthday(LocalDate.of(1998,8,12))
                 .build();
        Film film = Film.builder()
                .name("name1")
                .description("descriptionFilm1")
                .duration(25)
                .mpa(mpa)
                .releaseDate(LocalDate.of(1910, 1, 1))
                .likes(new ArrayList<>())
                .build();
        userDbStorage.create(user);
        userDbStorage.create(nextUser);
        filmStorage.create(film);
        Film film1 = filmStorage.getById(1);
        User user1 = userDbStorage.getById(1);
        User user2 = userDbStorage.getById(2);
        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user2.getId());
         String countRow = "select COUNT(USER_ID) from LIKE_FILM where FILM_ID = ?";
         SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(countRow, film1.getId());
         if(sqlRowSet.next()) {
             film1.setRate(sqlRowSet.getInt("COUNT(USER_ID)"));
         }
        assertEquals(film1.getRate(), 2, "Неверное количество лайков после добавления");
    }

    @Test
    void deleteLikeTest() {
        userDbStorage.create(firstUser);
        filmStorage.create(lastFilm);
        Film film = filmStorage.getById(1);
        User user = userDbStorage.getById(1);
        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.deleteLike(film.getId(), user.getId());
        int rate = 0;
        String countDel = "select COUNT(USER_ID) from LIKE_FILM where FILM_ID = ?";
        SqlRowSet sqlRowDel = jdbcTemplate.queryForRowSet(countDel, film.getId());
        if(sqlRowDel.next()) {
            rate = (sqlRowDel.getInt("COUNT(USER_ID)"));
        }
        film.setRate(rate);
        assertEquals(film.getRate(), 0, "Неверное количество лайков после удаления");
    }

    @Test
    void outPopular() {
        userDbStorage.create(firstUser);
        filmStorage.create(lastFilm);
        filmStorage.create(secondFilm);
        Film film = filmStorage.getById(1);
        Film film1 = filmStorage.getById(2);
        User user = userDbStorage.getById(1);
        filmStorage.addLike(film.getId(), film.getId());
        List<Film> popular = filmStorage.outputOfPopularMovies(2);
        assertEquals(popular.get(0).getName(), film.getName());
    }

}