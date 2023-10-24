package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataIsNullException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userStorage = new HashMap<>();

    private static Integer userId = 0;

    @Override
    public User create(User user) {
        validate(user);
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (checking(user.getId())) {
            validate(user);
            userStorage.put(user.getId(), user);
            return user;
        } else {
            throw new DataNotFoundException("Пользователя нет в списке!");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public boolean checking(Integer id) {
        if (!userStorage.containsKey(id)) {
            throw new DataNotFoundException("Пользователя под номером " + id + " не существует");
        }
        return userStorage.containsKey(id);
    }


    @Override
    public void delete(Integer id) {
        if (id == null) {
            throw new DataIsNullException("Невозможно передать null");
        }

        if (checking(id)) {
            userStorage.remove(id);
        } else {
            throw new DataNotFoundException("Пользователя не существует!");
        }
    }

    @Override
    public User getById(Integer id) {
        if (checking(id)) {
            return userStorage.get(id);
        } else {
            throw new DataNotFoundException("Пользователя с таким номером не существует!");
        }
    }

    @Override
    public void validate(User user) {
        if (user.getLogin().isBlank() || user.getEmail().isBlank() ||
                user.getBirthday().getYear() == 0) {
            throw new ValidationException("Login не может быть пустым!");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на {}", user.getLogin());
        }
    }

    public void deleteAll() {
        userStorage.clear();
    }

     private static Integer getNextId() {
        return ++userId;
    }
}
