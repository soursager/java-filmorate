package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895,12,28);

    private final Map<Integer, Film> filmStorage = new HashMap<>();

    private int filmId;

    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}",film);
        validate(film);
        film.setId(++filmId);
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    @GetMapping
    public List<Film> getAll() {
        log.info("Вывод всех фильмов");
        return  new ArrayList<>(filmStorage.values());
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}",film);
        if (!filmStorage.containsKey(film.getId())) {
            throw new DataNotFoundException("Запись в списке не найдена!");
        } else {
            validate(film);
            filmStorage.put(film.getId(), film);
            return film;
        }
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

}
