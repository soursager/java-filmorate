package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.NegativeNumberException;
import ru.yandex.practicum.filmorate.exception.NotFoundLikeException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class FilmDbStorage implements FilmStorage   {
    private final JdbcTemplate jdbcTemplate;

    private final MpaDbStorage mpaDbStorage;

    private final GenreDbStorage genreDbStorage;

    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895,12,28);

    @Override
    public Film create(Film film) {
        validate(film);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Integer filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

        if (!film.getGenres().isEmpty()) {
            film.setGenres(setGenresForFilm(film));
            for (Genre genre : film.getGenres()) {
                String sql = "merge into FILM_GENRE ( FILM_ID, GENRE_ID)" + "values (?, ?)";
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
        return getById(filmId);
    }

    @Override
    public Film update(Film film) {
        if (checkingForEntry(film.getId())) {
            validate(film);
            film.setMpa(setMpaForFilm(film));
            String sqlQuery = "update FILMS " +
                    "set FILM_NAME = ?," +
                    "DESCRIPTION = ?," +
                    "MPA_ID = ?," +
                    "RELEASE_DATE = ?," +
                    "DURATION = ?," +
                    "RATE = ? " +
                    "where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                    film.getMpa().getId(), film.getReleaseDate(), film.getDuration(),
                    film.getRate(), film.getId());
            String sqlDelGenre = "delete from FILM_GENRE where FILM_ID = ? ";
            jdbcTemplate.update(sqlDelGenre, film.getId());
            if (!film.getGenres().isEmpty()) {
                film.setGenres(setGenresForFilm(film));
                for (Genre genre : film.getGenres()) {
                    String sql = "merge into FILM_GENRE ( FILM_ID, GENRE_ID)" + "values (?, ?)";
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }
            return film;
        } else {
            throw new DataNotFoundException("Фильм не найден");
        }
    }

    @Override
    public void delete(Integer id) {
        checkingForEntry(id);
        String sqlQuery = "delete from FILMS where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film getById(Integer id) {
        String sqlQuery = "select * from films where FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::createfilm, id);
        if (films.size() != 1) {
            throw new DataNotFoundException("С таким номером несколько фильмов!");
        }
        Film film = films.get(0);
        film.setMpa(setMpaForFilm(film));
        String sql = "select g.GENRE_ID, GENRE_NAME from FILM_GENRE fg join " +
                "GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "where FILM_ID = ? ";
        List<Genre> genreList = jdbcTemplate.query(sql, FilmDbStorage::rowGenre,
                id);
        Set<Genre> genres = new TreeSet<>(genreList);
        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from films";
        List<Film> films = new ArrayList<>();
        for (Film film : jdbcTemplate.query(sqlQuery, FilmDbStorage::createfilm)) {
            film.setMpa(setMpaForFilm(film));
            String sql = "select g.GENRE_ID, GENRE_NAME from FILM_GENRE fg join " +
                    "GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                    "where FILM_ID = ? ";
            List<Genre> genreList = jdbcTemplate.query(sql, FilmDbStorage::rowGenre,
                    film.getId());
            Set<Genre> genres = new TreeSet<>(genreList);
            film.setGenres(genres);
            films.add(film);
        }
        return films;
    }

    @Override
    public boolean checkingForEntry(Integer id) {
        if (getById(id) == null) {
            throw new DataNotFoundException("Фильма под номером " + id + " не существует");
        }
        return true;
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Дата реализа раньше 28 декабря 1895 года");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания фильма - 200 символов!");
        }

        if (film.getName().isEmpty() || film.getDescription().isEmpty()
                || film.getReleaseDate().getYear() == 0) {
            throw new ValidationException("Невозможно добавить фильм с пустыми полями!");
        }
    }

    public void addLike(int filmId, int userId) {
        if (!checkLikes(filmId, userId)) {
            String sqlLike = "merge into LIKE_FILM (FILM_ID, USER_ID)" + "values(?, ?)";
            jdbcTemplate.update(sqlLike, filmId, userId);
        } else {
            throw new NotFoundLikeException("Этот пользователь уже ставил лайк этому фильму");
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (checkLikes(filmId, userId)) {
                String sqlQuery = "delete from LIKE_FILM where FILM_ID = ? and USER_ID = ?";
                jdbcTemplate.update(sqlQuery, filmId, userId);
        } else {
            throw new NotFoundLikeException("Этот пользователь не ставил лайк этому фильму");
        }
    }

    public List<Film> outputOfPopularMovies(Integer count) {
        List<Film> allFilms = getAll();
        for (Film film : allFilms) {
            String userRows = "select COUNT(USER_ID) from LIKE_FILM where FILM_ID = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(userRows, film.getId());
            if (sqlRowSet.next()) {
                film.setRate(sqlRowSet.getInt("COUNT(USER_ID)"));
            }
        }
        int size = allFilms.size();
        if (count < 0) {
            throw new NegativeNumberException("Размер не может быть отрицательным!");
        }
        if (size < count) {
            count = size;
        }
        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    static Film createfilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("MPA_ID"))
                        .name("name")
                        .build())
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .rate(rs.getInt("RATE"))
                .build();
    }

    private Set<Genre> setGenresForFilm(Film film) {
        Set<Genre> genres = new TreeSet<>();
        for (Genre genre : film.getGenres()) {
            genres.add(genreDbStorage.getById(genre.getId()));
        }
        return genres;
    }

    private Mpa setMpaForFilm(Film film) {
        return mpaDbStorage.getById(film.getMpa().getId());
    }

    static Genre rowGenre(ResultSet rs, int i) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("GENRE_NAME"))
                .build();
    }

    private boolean checkLikes(Integer filmId, Integer userId) {
        String sqlDelLike = "select FILM_ID = ? from LIKE_FILM where USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlDelLike, filmId, userId);
        return userRows.next();
    }
}
