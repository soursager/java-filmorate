package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {
    private final Map<Integer, User> userStorage = new HashMap<>();

    private int userId;

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя {}", user);
        validate(user);
        user.setId(++userId);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление данных пользователя {}", user);
        if (!userStorage.containsKey(user.getId())) {
            throw new DataNotFoundException("Запись в списке не найдена!");
        } else {
            validate(user);
            userStorage.put(user.getId(), user);
            return user;
        }
    }

    @Override
    @GetMapping
    public List<User> getAll() {
        log.info("Вывод всех пользователей");
        return  new ArrayList<>(userStorage.values());
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

}
