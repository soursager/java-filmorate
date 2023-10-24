package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.List;

public interface AbstractStorage<T extends BaseUnit> {
    T create(T date);

    T update(T date);

    void delete(Integer id);

    T getById(Integer id);

    List<T> getAll();

    boolean checking(Integer id);

    void validate(T date);
}
