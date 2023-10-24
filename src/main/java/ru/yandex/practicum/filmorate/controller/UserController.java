package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController<User> {

    private final UserService userService;

    @Override
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя {}", user);
        return userService.createUser(user);
    }

    @Override
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление данных пользователя {}", user);
        return userService.updateUser(user);
    }

    @Override
    @GetMapping
    public List<User> getAll() {
        log.info("Вывод всех пользователей");
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        log.info("Вывод пользователя под номером {}",id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.info("Удаление пользователя под номером {}",id);
        userService.deleteUser(id);
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addInFriends(@PathVariable("id") Integer id,
                             @PathVariable("friendId") Integer friendId) {
        log.info("Добавление в друзья пользователя {}",friendId);
        userService.addInFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable("id") Integer id,
                              @PathVariable("friendId") Integer friendId) {
        log.info("Удаление из друзей пользователя {}",friendId);
        userService.removingFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") Integer id) {
        log.info("Вывод всех друзей");
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") Integer id,
                                      @PathVariable("otherId") Integer otherId) {
        log.info("Вывод общих друзей с пользователем {}", otherId);
        return userService.mutualFriends(id, otherId);
    }
}
