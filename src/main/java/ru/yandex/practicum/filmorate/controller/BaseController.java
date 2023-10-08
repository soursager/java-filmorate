package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.BaseUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseController<T extends BaseUnit>  {
    public abstract void validate(T data);
    private final Map<Integer,T> storage = new HashMap<>();
    private int generatedId;
    public T create(T date) {
        validate(date);
        generate();
        date.setId(generatedId);
        storage.put(date.getId(),date);
        return date;
    }

    public T update(T date) {
        if(!storage.containsKey(date.getId())) {
            throw new DataNotFoundException("Запись в списке не найдена!");
        }
        else {
            validate(date);
            storage.put(date.getId(), date);
            return date;
        }
    }

    public List<T> getAll() {
        return  new ArrayList<>(storage.values());
    }
    public void generate() {
        generatedId++;
    }

}
