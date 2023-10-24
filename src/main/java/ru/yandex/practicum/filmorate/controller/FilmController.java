package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController extends BaseController<Film> {

    private final FilmService filmService;

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}",film);
        return filmService.createFilm(film);
    }

    @Override
    @GetMapping
    public List<Film> getAll() {
        log.info("Вывод всех фильмов");
        return filmService.getAllFilms();
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}",film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") Integer id) {
        log.info("Получение фильма по номеру {}",id);
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.info("Удаление фильма по номеру {}",id);
        filmService.deleteFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id,
                         @PathVariable("userId") Integer userId) {
        log.info("Добавление лайка от пользователя {}",userId);
        filmService.addLikeToMovie(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id,
                           @PathVariable("userId") Integer userId) {
        log.info("Удаление лайка от пользователя {}",userId);
        filmService.removeLikeToMovie(id, userId);
    }

    @GetMapping("/popular")
        public List<Film> getPopular(
                @RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Вывод самых популярных фильмов");
        return filmService.outputOfPopularMovies(count);
    }
}
