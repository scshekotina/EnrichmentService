package org.example.user;

import org.example.exception.user.UserNotFoundException;
import org.example.exception.user.UserNotSavedException;

import java.util.concurrent.ConcurrentHashMap;

public class UserStorageInMemory implements UserStorage {

    private ConcurrentHashMap<String, User> users;

    private static final String DOUBLE_MSIDSN = "Пользователь с таким номером телефона уже существует!";
    private static final String NOT_FOUND_MSIDSN = "Пользователя с таким номером телефона не существует!";
    private static final String EMPTY_MSIDSN = "Пустой номеру телефона!";
    private static final String EMPTY_USER_OR_MSIDSN = "Пустой пользователь или номер телефона!";
    private static final String INCORRECT_MSIDSN = "Некорректный номер телефона!";

    public UserStorageInMemory(ConcurrentHashMap<String, User> users) {
        this.users = users;
    }

    @Override
    public User get(String msisdn) {
        if (msisdn == null) {
            throw new UserNotFoundException(EMPTY_MSIDSN);
        }
        User user = users.get(msisdn.trim());
        if (user == null) {
            throw new UserNotFoundException();
        };
        return user;
    }

    @Override
    public void add(User user) {
        checkUser(user);
        if (users.putIfAbsent(user.getPhone().trim(), user) != null) {
            throw new UserNotSavedException(DOUBLE_MSIDSN);
        }
    }

    @Override
    public User update(String msidsn, User user) {
        checkUser(user);
        if(!user.getPhone().trim().equals(msidsn)) {
            throw new UserNotSavedException();
        }
        User saved = users.replace(msidsn, user);
        if (saved == null) {
            throw new UserNotFoundException(NOT_FOUND_MSIDSN);
        }
        return saved;
    }

    @Override
    public void delete(User user) {
        checkUser(user);
        if (users.remove(user.getPhone().trim()) == null) {
            throw new UserNotFoundException();
        };
    }

    private static void checkUser(User user) {
        if (user == null || user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new UserNotSavedException(EMPTY_USER_OR_MSIDSN);
        }
    }
}