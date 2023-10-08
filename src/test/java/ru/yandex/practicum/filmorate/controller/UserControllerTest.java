package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    void trueValidate() {
        userController.create(user);
        assertEquals(userController.getAll().size(),1);
    }

    @Test
    void falseValidate() {
        user.setLogin("");
        assertThrows(ValidationException.class,() -> userController.validate(user));
    }

    @Test
    void emptyValidate() {
        user.setName("");
        user.setLogin("");
        user.setEmail("");
        assertThrows(ValidationException.class,() -> userController.validate(user));
    }

    @Test
    void emptyName() {
        User user1 = User.builder()
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .build();
        userController.create(user1);
        assertEquals(user1.getName(), user1.getLogin());
    }
}