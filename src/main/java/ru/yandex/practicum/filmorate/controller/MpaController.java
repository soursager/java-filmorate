package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService service;

    @GetMapping
    public List<Mpa> getAll() {
       final List<Mpa> mpas = service.getAllMpa();
        log.info("Вывод всех рейтингов");
       return mpas;
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable("id") Integer id) {
        log.info("Вывод рейтинга под номером {}",id);
        return service.getMpaById(id);
    }
}
