package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getAllUser() {
        return userStorage.getAll();
    }

    public void deleteUser(Integer id) {
        userStorage.delete(id);
    }

    public User getUserById(Integer id) {
            return userStorage.getById(id);
    }

    public void addInFriends(Integer userId, Integer friendId) {
          if (userStorage.checkingForEntry(userId) && userStorage.checkingForEntry(friendId)) {
             friendStorage.addFriend(userId, friendId);
         } else {
             throw new DataNotFoundException("Не существует обоих либо одного пользователя!");
         }
    }

    public void removingFromFriends(Integer userId, Integer friendId) {
         if (userStorage.checkingForEntry(userId) && userStorage.checkingForEntry(friendId)) {
            friendStorage.removeInFriendForList(userId, friendId);
         } else {
            throw new DataNotFoundException("Не существует обоих либо одного пользователя!");
        }
    }

    public List<User> getAllFriends(Integer userId) {
        return friendStorage.getFriends(userId);
    }

    public List<User> mutualFriends(Integer firstUserId, Integer secondUserId) {
        return friendStorage.mutualFriends(firstUserId, secondUserId);
    }

    @Deprecated
    private boolean checkFriends(Integer userId, Integer friendIs) {
        return userStorage.getById(userId).getFriends()
                .contains(userStorage.getById(friendIs));
    }

    @Deprecated
    private void addInFriendForList(Integer userId, Integer friendId) {
        userStorage.getById(userId).getFriends()
                .add(userStorage.getById(friendId));
        userStorage.getById(friendId).getFriends()
                .add(userStorage.getById(userId));
    }

    @Deprecated
    private void removeInFriendForList(Integer userId, Integer friendId) {
        userStorage.getById(userId).getFriends()
                .remove(userStorage.getById(friendId));
        userStorage.getById(friendId).getFriends()
                .remove(userStorage.getById(userId));
    }
}
