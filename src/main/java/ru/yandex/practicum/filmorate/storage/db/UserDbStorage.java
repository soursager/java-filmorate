package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static Integer userId = 0;

    @Override
    public User create(User user) {
            validate(user);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");
            user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        if (checkingForEntry(user.getId())) {
            validate(user);
            String sqlQuery = "update users " +
                    "set email = ?," +
                    "login = ?," +
                    "name = ?," +
                    "birthday = ? " +
                    "where user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                    user.getBirthday(), user.getId());
            return user;
        } else {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void delete(Integer id) {
        checkingForEntry(id);
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User getById(Integer id) {
        String sqlQuery = "select * from users where user_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery,UserDbStorage::createUser, id);
        if (users.isEmpty()) {
            throw new DataNotFoundException("Что-то пошло не так!");
        }
        return users.get(0);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "Select * from users";
        return jdbcTemplate.query(sqlQuery,UserDbStorage::createUser);
    }

    @Override
    public boolean checkingForEntry(Integer id) {
        if (getById(id) == null) {
            throw new DataNotFoundException("Пользователя под номером " + id + " не существует");
        } else {
            return true;
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

    private static Integer getNextId() {
        return ++userId;
    }

    static User createUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
