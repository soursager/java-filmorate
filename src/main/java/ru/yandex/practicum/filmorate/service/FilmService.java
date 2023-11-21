package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmDbStorage = filmDbStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmDbStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmDbStorage.update(film);
    }

    public void deleteFilm(Integer id) {
         filmDbStorage.delete(id);
    }

    public Film getFilmById(Integer id) {
        return filmDbStorage.getById(id);
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAll();
    }

    public void addLikeToMovie(Integer filmId, Integer userId) {
        filmDbStorage.checkingForEntry(filmId);
        userStorage.checkingForEntry(userId);
        filmDbStorage.addLike(filmId, userId);
    }

    public void removeLikeToMovie(Integer filmId, Integer userId) {
        filmDbStorage.checkingForEntry(filmId);
        userStorage.checkingForEntry(userId);
        filmDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> outputOfPopularMovies(Integer count) {
    return filmDbStorage.getPopularMovies(count);
    }
}
