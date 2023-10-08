package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895,12,28);

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}",film);
        return super.create(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Вывод всех фильмов");
        return super.getAll();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма {}",film);
        return super.update(film);
    }

    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(START_RELEASE_DATE)) {
            throw new ValidationException("Дата реализа раньше 28 декабря 1895 года");
        }

        if (data.getName().isEmpty() || data.getDescription().isEmpty()
                || data.getReleaseDate().getYear() == 0) {
            throw new ValidationException("Невозможно добавить фильм с пустыми полями!");
        }
    }
}
