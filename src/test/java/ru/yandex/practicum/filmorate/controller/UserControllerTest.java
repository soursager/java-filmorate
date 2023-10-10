package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = User.builder()
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
    }

    @Test
    void validateUserTrueField() {
        userController.create(user);
        assertEquals(userController.getAll().size(),1);
    }

    @Test
    void validateUserEmptyLogin() {
        user.setLogin("");
        assertThrows(ValidationException.class,() -> userController.validate(user));
    }

    @Test
    void validateUserEmptyField() {
        user.setName("");
        user.setLogin("");
        user.setEmail("");
        assertThrows(ValidationException.class,() -> userController.validate(user));
    }

    @Test
    void testUserEmptyName() {
        User user1 = User.builder()
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
        userController.create(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    void testPostUser() {
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    void testUpdateUser() {
        User user1 = User.builder()
                .id(1)
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
        userController.create(user);
        assertEquals(user1, userController.update(user1));
    }

    @Test
    void testUpdateUserNotAssigned() {
        User user1 = User.builder()
                .id(99)
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
        userController.create(user);
        assertThrows(DataNotFoundException.class, () -> userController.update(user1));
    }
}