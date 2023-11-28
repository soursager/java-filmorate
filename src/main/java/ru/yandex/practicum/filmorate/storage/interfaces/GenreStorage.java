package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage  {

    List<Genre> getAll();

    Genre getById(Integer id);
}
