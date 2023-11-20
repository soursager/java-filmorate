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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    private User firstUser;

    private User secondUser;

    @BeforeEach
    void setUp() {

        userDbStorage = new UserDbStorage(jdbcTemplate);

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
    void createTest() {
        userDbStorage.create(firstUser);
        assertEquals(userDbStorage.getById(1).getName(), "nameUser1");
    }

    @Test
    void update() {
        userDbStorage.create(secondUser);
        User lastUser = User.builder()
                .id(1)
                .name("nameUser3")
                .email("fork3@yandex.ru")
                .login("fork3")
                .birthday(LocalDate.of(2010,8,12))
                .build();
        userDbStorage.update(lastUser);
        assertEquals(userDbStorage.getById(1).getName(),"nameUser3");
    }

    @Test
    void delete() {
        userDbStorage.create(firstUser);
        userDbStorage.delete(firstUser.getId());
        List<User> users = userDbStorage.getAll();
        assertEquals(users.size(), 0);
    }

    @Test
    void getById() {
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        assertEquals(userDbStorage.getById(firstUser.getId()).getName(), firstUser.getName());
        assertEquals(userDbStorage.getById(secondUser.getId()).getName(), secondUser.getName());
    }

    @Test
    void getAll() {
        userDbStorage.create(firstUser);
        userDbStorage.create(secondUser);
        assertEquals(userDbStorage.getAll().size(), 2);
    }
}