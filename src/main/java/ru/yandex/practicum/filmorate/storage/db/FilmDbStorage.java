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
    private static final String MAIN_SELECT = "select films.film_id, " +
            "films.film_name, " +
            "films.description, " +
            "films.duration, " +
            "films.release_date, " +
            "films.mpa_id as mpa_id, " +
            "mpa.mpa_name as mpa_name, " +
            "genre.genre_id as genre_id, " +
            "genre.genre_name as genre_name " +
            "from films " +
            "left join mpa on films.mpa_id = mpa.mpa_id " +
            "left join film_genre on films.film_id = film_genre.film_id " +
            "left join genre on film_genre.genre_id = genre.genre_id ";

    private final JdbcTemplate jdbcTemplate;

    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895,12,28);

    @Override
    public Film create(Film film) {
        validate(film);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Integer filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sql = "merge into film_genre ( film_id, genre_id)" + "values (?, ?)";
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
        return getById(filmId);
    }

    @Override
    public Film update(Film film) {
        if (checkingForEntry(film.getId())) {
            validate(film);
            String sqlQuery = "update films " +
                    "set film_name = ?," +
                    "description = ?," +
                    "mpa_id = ?," +
                    "release_date = ?," +
                    "duration = ?," +
                    "rate = ? " +
                    "where film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                    film.getMpa().getId(), film.getReleaseDate(), film.getDuration(),
                    film.getRate(), film.getId());
            String sqlDelGenre = "delete from film_genre where film_id = ? ";
            jdbcTemplate.update(sqlDelGenre, film.getId());
            if (!film.getGenres().isEmpty()) {
                for (Genre genre : film.getGenres()) {
                    String sql = "merge into film_genre ( film_id, genre_id)" + "values (?, ?)";
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
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film getById(Integer id) {

        String sql = MAIN_SELECT + "where films.film_id = ?";//order by genre_id";

        List<Film> films = getCompleteFilmFromQuery(sql, id);

        if (films.isEmpty()) {
            throw new DataNotFoundException("Фильма не существует!");
        }

        return films.get(0);
    }

    @Override
    public List<Film> getAll() {
        String sql = MAIN_SELECT + "order by films.film_id";

        return getCompleteFilmFromQuery(sql);
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
            String sqlLike = "merge into like_film (film_id, user_id)" + "values(?, ?)";
            jdbcTemplate.update(sqlLike, filmId, userId);
        } else {
            throw new NotFoundLikeException("Этот пользователь уже ставил лайк этому фильму");
        }
    }

    public void deleteLike(int filmId, int userId) {
        if (checkLikes(filmId, userId)) {
                String sqlQuery = "delete from like_film where film_id = ? and user_id = ?";
                jdbcTemplate.update(sqlQuery, filmId, userId);
        } else {
            throw new NotFoundLikeException("Этот пользователь не ставил лайк этому фильму");
        }
    }

    public List<Film> getPopularMovies(Integer count) {
        List<Film> allFilms = getAll();
        for (Film film : allFilms) {
            String userRows = "select count(user_id) from like_film where film_id = ?";
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(userRows, film.getId());
            if (sqlRowSet.next()) {
                film.setRate(sqlRowSet.getInt("count(user_id)"));
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

private List<Film> getCompleteFilmFromQuery(String sql, Object... params) {
    return jdbcTemplate.query(sql,
            rs -> {
                Map<Integer, Film> map = new HashMap<>();
                while (rs.next()) {
                    if (!map.containsKey(rs.getInt("film_id"))) {
                        Film film = constructFilmFromQueryResult(rs);

                        if (rs.getInt("mpa_id") != 0) {
                            film.setMpa(constructRatingMpaFromQueryResult(rs));
                        }

                        if (rs.getInt("genre_id") != 0) {
                            film.getGenres().add(constructGenreFromQueryResult(rs));
                        }

                        map.put(film.getId(), film);
                    } else {
                        Film film = map.get(rs.getInt("film_id"));
                        if (rs.getInt("genre_id") != 0) {
                            film.getGenres().add(constructGenreFromQueryResult(rs));
                        }
                    }
                }
                return map.isEmpty() ? new ArrayList<>() : new ArrayList<>(map.values());
            }, params
    );
}

    private Genre constructGenreFromQueryResult(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private Mpa constructRatingMpaFromQueryResult(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private Film constructFilmFromQueryResult(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .genres(new TreeSet<>())
                .build();
    }

    private boolean checkLikes(Integer filmId, Integer userId) {
        String sqlDelLike = "select film_id = ? from like_film where user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlDelLike, filmId, userId);
        return userRows.next();
    }
}
