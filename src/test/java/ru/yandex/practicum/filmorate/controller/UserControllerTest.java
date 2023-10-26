package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.FriendOnTheListException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;


import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class UserControllerTest {
    @Autowired
    private InMemoryUserStorage inMemoryUserStorage;
    @Autowired
    private UserController userController;
    private User user;
    private User secondUser;
    private User lastUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("nameUser")
                .email("fork@yandex.ru")
                .login("fork")
                .birthday(LocalDate.of(1994,8,12))
                .friends(new ArrayList<>())
                .build();

        secondUser = User.builder()
                .name("secondUser")
                .email("fork1@yandex.ru")
                .login("fork1")
                .friends(new ArrayList<>())
                .birthday(LocalDate.of(1998,8,12))
                .build();

        lastUser = User.builder()
                .name("lastUser")
                .email("fork2@yandex.ru")
                .login("fork2")
                .friends(new ArrayList<>())
                .birthday(LocalDate.of(1999,8,12))
                .build();
        inMemoryUserStorage.deleteAll();
    }

  @Test
    void validateUserTrue() {
        userController.create(user);
        assertEquals(userController.getAll().size(), 1);
    }

    @Test
    void validateUserEmptyLogin() {
        user.setLogin("");
        assertThrows(ValidationException.class,() -> userController.create(user));
    }

    @Test
    void validateUserEmptyField() {
        user.setName("");
        user.setLogin("");
        user.setEmail("");
        assertThrows(ValidationException.class,() -> userController.create(user));
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
                .birthday(LocalDate.of(2010,8,12))
                .build();
        userController.create(user1);
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

    @Test
    void testGetByIdUser() {
        userController.create(user);
        assertEquals(userController.getById(user.getId()), user);
    }

    @Test
    void failedGetByIdUser() {
        userController.create(user);
        assertThrows(DataNotFoundException.class, () -> userController.getById(99));
    }

    @Test
    void testDeleteByIdUser() {
        userController.create(user);
        userController.deleteById(user.getId());
        assertEquals(userController.getAll().size(), 0);
    }

    @Test
    void failedDeleteByIdUser() {
        userController.create(user);
        assertThrows(DataNotFoundException.class, () -> userController.deleteById(99));
    }

    @Test
    void testAddInFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        assertEquals(user.getFriends().size(), 1);
        assertEquals(secondUser.getFriends().size(), 1);
    }

    @Test
    void failedAddInFriends() {
        userController.create(user);
        assertThrows(DataNotFoundException.class, () ->
                userController.addInFriends(user.getId(), 3));
    }

    @Test
    void testAlreadyFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        assertThrows(FriendOnTheListException.class, () ->
                userController.addInFriends(secondUser.getId(), user.getId()));
    }

    @Test
    void testDeleteFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        userController.deleteFriends(user.getId(), secondUser.getId());
        assertEquals(user.getFriends().size(), 0);
        assertEquals(secondUser.getFriends().size(), 0);
    }

    @Test
    void failedDeleteFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        assertThrows(DataNotFoundException.class, () ->
                userController.deleteFriends(user.getId(), 99));
    }

    @Test
    void testDeleteNotAFriends() {
        userController.create(user);
        userController.create(secondUser);
        assertThrows(FriendOnTheListException.class, () ->
                userController.deleteFriends(user.getId(), secondUser.getId()));
    }

    @Test
    void testGetAllFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.create(lastUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        userController.addInFriends(user.getId(), lastUser.getId());
        assertEquals(userController.getAllFriends(user.getId()).size(), 2);
        assertEquals(userController.getAllFriends(secondUser.getId()).size(), 1);
    }

    @Test
    void testGetMutualFriends() {
        userController.create(user);
        userController.create(secondUser);
        userController.create(lastUser);
        userController.addInFriends(user.getId(), secondUser.getId());
        userController.addInFriends(user.getId(), lastUser.getId());
        assertEquals(userController.getMutualFriends(secondUser.getId(), lastUser.getId()).size(), 1);
        assertEquals(userController.getMutualFriends(secondUser.getId(), lastUser.getId()).get(0),
                user);
    }
}