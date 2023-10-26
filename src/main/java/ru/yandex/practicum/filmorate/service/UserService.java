package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.FriendOnTheListException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

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
        if (checkFriends(userId, friendId)) {
            throw new FriendOnTheListException("Пользователь уже есть в друзьях!");
        }
          if (userStorage.checkingForEntry(userId) && userStorage.checkingForEntry(friendId)) {
             addInFriendForList(userId, friendId);
         } else {
             throw new DataNotFoundException("Не существует обоих либо одного пользователя!");
         }
    }

    public void removingFromFriends(Integer userId, Integer friendId) {
        if (!checkFriends(userId, friendId)) {
            throw new FriendOnTheListException("Пользователя нет в списке друзей!");
        }
         if (userStorage.checkingForEntry(userId) && userStorage.checkingForEntry(friendId)) {
            removeInFriendForList(userId, friendId);
         } else {
            throw new DataNotFoundException("Не существует обоих либо одного пользователя!");
        }
    }

    public List<User> getAllFriends(Integer userId) {
        return userStorage.getById(userId).getFriends();
    }

    public List<User> mutualFriends(Integer firstUserId, Integer secondUserId) {
        return friendsList(firstUserId).stream()
                .filter(friendsList(secondUserId)::contains)
                .collect(Collectors.toList());
    }

    private boolean checkFriends(Integer userId, Integer friendIs) {
        return userStorage.getById(userId).getFriends()
                .contains(userStorage.getById(friendIs));
    }

    private void addInFriendForList(Integer userId, Integer friendId) {
        userStorage.getById(userId).getFriends()
                .add(userStorage.getById(friendId));
        userStorage.getById(friendId).getFriends()
                .add(userStorage.getById(userId));
    }

    private void removeInFriendForList(Integer userId, Integer friendId) {
        userStorage.getById(userId).getFriends()
                .remove(userStorage.getById(friendId));
        userStorage.getById(friendId).getFriends()
                .remove(userStorage.getById(userId));
    }

    private List<User> friendsList(Integer userId) {
        return userStorage.getById(userId).getFriends();
    }
}
