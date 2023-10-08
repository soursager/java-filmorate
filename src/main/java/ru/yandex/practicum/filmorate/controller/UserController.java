package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if(user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на {}", user.getLogin());
        }
        log.info("Добавление пользователя {}", user);
        return super.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление данных пользователя {}", user);
        return super.update(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Вывод всех пользователей");
        return super.getAll();
    }

    @Override
    public void validate(User user) {
        if(user.getLogin().isBlank() || user.getEmail().isBlank() ||
        user.getBirthday().getYear() == 0) {
            throw new ValidationException("Не может быть пустым!");
        }

        /*if(user.getName() == null) {
            throw new ValidationException("Имя не может быть null");
        } */
    }
}
