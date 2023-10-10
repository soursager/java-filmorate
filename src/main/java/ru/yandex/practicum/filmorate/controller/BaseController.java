package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.List;


public abstract class BaseController<T extends BaseUnit>  {
    public abstract void validate(T data);

    public abstract T create(T date);

    public abstract T update(T date);

    public abstract List<T> getAll();

}
