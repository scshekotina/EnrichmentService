package org.example.user;

import org.example.TestData;
import org.example.exception.user.UserNotFoundException;

import org.example.exception.user.UserNotSavedException;
import org.junit.*;

import static org.example.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserStorageInMemoryTest {

    private static UserStorage userStorage;

    @Before
    public void setUp() throws Exception {
        userStorage = TestData.getStandardUserStorage();
    }

    @Test
    public void get() {
        assertEquals(TestData.getVasya(), userStorage.get(VASYA_PHONE));
    }

    @Test
    public void getNotFound() {
        assertThrows(UserNotFoundException.class, () -> userStorage.get(NOT_FOUND_PHONE));
    }

    @Test
    public void add() {
        userStorage.add(TestData.getPetya());
        assertEquals(TestData.getPetya(), userStorage.get(PETYA_PHONE));
    }

    @Test
    public void addEmpty() {
        User petya = getPetya();
        petya.setPhone("");
        assertThrows(UserNotSavedException.class, () -> userStorage.add(petya));
    }

    @Test
    public void update() {
        assertEquals(TestData.getKolya(), userStorage.get(KOLYA_PHONE));
        userStorage.update(KOLYA_PHONE, TestData.getNikolay());
        assertEquals(TestData.getNikolay(), userStorage.get(KOLYA_PHONE));
    }

    @Test
    public void updatePhoneNotSaved() {
        assertEquals(TestData.getKolya(), userStorage.get(KOLYA_PHONE));
        User kolyaForUpdate = TestData.getNikolay();
        kolyaForUpdate.setPhone("8952");
        assertThrows(UserNotSavedException.class, () -> userStorage.update(KOLYA_PHONE, kolyaForUpdate));
    }


    @Test
    public void delete() {
        assertEquals(TestData.getKolya(), userStorage.get(KOLYA_PHONE));
        userStorage.delete(TestData.getKolya());
        assertThrows(UserNotFoundException.class, () -> userStorage.get(KOLYA_PHONE));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(UserNotFoundException.class, () -> userStorage.delete(TestData.getPetya()));
    }

    @Test
    public void deleteIncorrect() {
        User petya = getPetya();
        petya.setPhone("");
        assertThrows(UserNotSavedException.class, () -> userStorage.delete(petya));
    }
}