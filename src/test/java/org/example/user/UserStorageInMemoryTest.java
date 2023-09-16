package org.example.user;

import org.example.exception.user.UserNotFoundException;

import java.util.concurrent.ConcurrentHashMap;

import org.example.exception.user.UserNotSavedException;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserStorageInMemoryTest {

    private static UserStorage userStorage;

    private static final User vasya = new User("Vasya", "Petrov", "12345");
    private static final User kolya =  new User("Kolya", "Ivanov", "8913");

    @Before
    public void setUp() throws Exception {
        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
        users.put("12345", vasya);
        users.put("8913", kolya);
        userStorage = new UserStorageInMemory(users);
    }

    @Test
    public void get() {
        assertEquals(vasya, userStorage.get("12345 "));
    }

    @Test
    public void getNotFound() {
        assertThrows(UserNotFoundException.class, () -> userStorage.get("1"));
    }

    @Test
    public void add() {
        User petya = new User("petya", "fishman", "138500");
        userStorage.add(petya);
        User expected = new User("petya", "fishman", "138500");
        assertEquals(expected, userStorage.get("138500"));
    }

    @Test
    public void addEmpty() {
        User petya = new User("petya", "fishman", "");
        assertThrows(UserNotSavedException.class, () -> userStorage.add(petya));
    }

    @Test
    public void update() {
        assertEquals(kolya, userStorage.get("8913"));
        User kolyaForUpdate = new User("Nikolay", "Ivanov", "8913");
        userStorage.update("8913", kolyaForUpdate);
        User expected = new User("Nikolay", "Ivanov", "8913");
        assertEquals(expected, userStorage.get("8913"));
    }

    @Test
    public void updatePhoneNotSaved() {
        assertEquals(kolya, userStorage.get("8913"));
        User kolyaForUpdate = new User("Nikolay", "Ivanov", "8952");
        assertThrows(UserNotSavedException.class, () -> userStorage.update("8913", kolyaForUpdate));
    }


    @Test
    public void delete() {
        assertEquals(kolya, userStorage.get("8913"));
        userStorage.delete(kolya);
        assertThrows(UserNotFoundException.class, () -> userStorage.get("8913"));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(UserNotFoundException.class, () -> userStorage.delete(
                new User("petya", "fishman", "1000")));
    }

    @Test
    public void deleteIncorrect() {
        assertThrows(UserNotSavedException.class, () -> userStorage.delete(
                new User("petya", "fishman", "")));
    }
}