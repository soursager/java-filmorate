package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate START_RELEASE_DATE = LocalDate.of(1895,12,28);

    private final Map<Integer, Film> filmStorage = new HashMap<>();

    private static Integer filmId = 0;

    @Override
    public Film create(Film film) {
        validate(film);
        film.setId(getNextId());
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!checkingForEntry(film.getId())) {
            throw new DataNotFoundException("Запись в списке не найдена!");
        } else {
            validate(film);
            filmStorage.put(film.getId(), film);
            return film;
        }
    }

    @Override
    public void delete(Integer id) {
        if (checkingForEntry(id)) {
            filmStorage.remove(id);
        } else {
            throw new DataNotFoundException("Фильм в списке не найден!");
        }
    }

    @Override
    public Film getById(Integer id) {
        if (checkingForEntry(id)) {
            return filmStorage.get(id);
        } else {
            throw new DataNotFoundException("Фильм в списке не найден!");
        }
    }

    @Override
    public List<Film> getAll() {
        return  new ArrayList<>(filmStorage.values());
    }

    @Override
    public boolean checkingForEntry(Integer id) {
        if (!filmStorage.containsKey(id)) {
            throw new DataNotFoundException("Фильма под номером " + id + " не существует");
        }
        return filmStorage.containsKey(id);
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

    public void deleteAll() {
        filmStorage.clear();
    }

    private static Integer getNextId() {
        return ++filmId;
    }
}
