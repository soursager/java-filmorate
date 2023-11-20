package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FriendOnTheListException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        if (!checkFriends(userId, friendId)) {
            String sqlQuery = "merge into USER_FRIEND(USER_ID, FRIEND_ID) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId);
        } else {
            throw new FriendOnTheListException("Пользователи уже являются друзьями!");
        }
    }

    @Override
    public void removeInFriendForList(int userId, int friendId) {
        if (checkFriends(userId, friendId)) {
            String sqlQuery = "delete from USER_FRIEND where USER_ID = ? AND FRIEND_ID = ? ";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } else {
            throw new FriendOnTheListException("Пользователи не друзья!");
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sqlQuery = "select * from USERS where USER_ID IN " +
                "(select FRIEND_ID from USER_FRIEND where USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, FriendDbStorage::createFriend,
                userId);
    }

    @Override
    public List<User> mutualFriends(int userId, int secondUserId) {
        return getFriends(userId).stream()
                .filter(getFriends(secondUserId)::contains)
                .collect(Collectors.toList());
    }

    public boolean checkFriends(int userId, int friendIs) {
        List<Integer> friendsId = getFriends(userId).stream()
                .map(user -> user.getId())
                .collect(Collectors.toList());
        return friendsId.contains(friendIs);
    }

    static User createFriend(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("USER_ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
