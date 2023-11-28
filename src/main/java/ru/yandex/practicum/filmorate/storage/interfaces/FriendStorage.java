package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void addFriend(int userId, int friendId);

    void removeInFriendForList(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> mutualFriends(int userId, int secondUserId);
}
