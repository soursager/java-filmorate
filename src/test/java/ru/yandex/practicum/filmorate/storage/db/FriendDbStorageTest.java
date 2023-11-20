package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;
    private FriendDbStorage friendDbStorage;

    public static void main(String[] args) {

    }

    private User firstUser;

    private User secondUser;
    @BeforeEach
    void setUp() {

        userDbStorage = new UserDbStorage(jdbcTemplate);
        friendDbStorage = new FriendDbStorage(jdbcTemplate);

        firstUser = User.builder()
                .name("nameUser1")
                .email("fork1@yandex.ru")
                .login("fork1")
                .birthday(LocalDate.of(1994,8,12))
                .build();

        secondUser = User.builder()
                .name("nameUser2")
                .email("fork2@yandex.ru")
                .login("fork2")
                .birthday(LocalDate.of(2000,8,12))
                .build();
    }
    @Test
    void addFriend() {
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        assertEquals(friendDbStorage.checkFriends(firstUser.getId(),secondUser.getId()), true);
    }

    @Test
    void removeInFriendForList() {
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        assertEquals(friendDbStorage.checkFriends(firstUser.getId(),secondUser.getId()), true);
        friendDbStorage.removeInFriendForList(firstUser.getId(), secondUser.getId());
        assertEquals(friendDbStorage.checkFriends(firstUser.getId(),secondUser.getId()), false);
    }

    @Test
    void getFriends() {
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        friendDbStorage.addFriend(secondUser.getId(), firstUser.getId());
        assertEquals(friendDbStorage.getFriends(firstUser.getId()).size(), 1);
        assertEquals(friendDbStorage.getFriends(secondUser.getId()).size(), 1);
    }

    @Test
    void mutualFriends() {
        User lastUser = User.builder()
                .name("nameUser5")
                .email("fork5@yandex.ru")
                .login("fork5")
                .birthday(LocalDate.of(2000,8,12))
                .build();
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        userDbStorage.create(lastUser);
        friendDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        friendDbStorage.addFriend(lastUser.getId(), secondUser.getId());
        assertEquals((friendDbStorage.mutualFriends(firstUser.getId(), lastUser.getId())).size(), 1);
    }
}