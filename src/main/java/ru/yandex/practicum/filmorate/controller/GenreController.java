package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @GetMapping
    public List<Genre> getAll() {
        final List<Genre> genres = service.getAllGenres();
        log.info("Вывод всех жанров");
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable("id") Integer id) {
        log.info("Вывод жанра под номером {}",id);
        return service.getGenreById(id);
    }
}
