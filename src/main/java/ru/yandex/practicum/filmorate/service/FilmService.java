package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.NegativeNumberException;
import ru.yandex.practicum.filmorate.exception.NotFoundLikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void deleteFilm(Integer id) {
         filmStorage.delete(id);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void addLikeToMovie(Integer filmId, Integer userId) {
        filmStorage.checkingForEntry(filmId);
        userStorage.checkingForEntry(userId);
        if (!checkLikes(filmId, userId)) {
            likeForFilm(filmId, userId);
        } else {
            throw new DataNotFoundException("Пользователь под номером"
                    + userId + "уже поставил лайк фильму под номером " + filmId + " .");
        }
    }

    public void removeLikeToMovie(Integer filmId, Integer userId) {
        filmStorage.checkingForEntry(filmId);
        userStorage.checkingForEntry(userId);
        if (checkLikes(filmId, userId)) {
        deleteLike(filmId, userId);
        } else {
            throw new NotFoundLikeException("Пользователь под номером "
                    + userId + " еще не ставил лайк фильму " + filmId + " .");
        }
    }

    public List<Film> outputOfPopularMovies(Integer count) {
        int size = getAllFilms().size();
        if (count < 0) {
            throw new NegativeNumberException("Размер не может быть отрицательным!");
        }
        if (size < count) {
            count = size;
        }
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean checkLikes(Integer filmId, Integer userId) {
        return filmStorage.getById(filmId).getLikes().contains(userId);
    }

    private void likeForFilm(Integer filmId, Integer userId) {
        filmStorage.getById(filmId).getLikes().add(userId);
    }

    private void deleteLike(Integer filmId, Integer userId) {
        filmStorage.getById(filmId).getLikes().remove(userId);
    }

}
