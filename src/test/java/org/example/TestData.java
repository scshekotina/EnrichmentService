package org.example;

import org.example.message.Message;
import org.example.user.User;
import org.example.user.UserStorage;
import org.example.user.UserStorageInMemory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestData {
    public static final Message CORRECT_VASYA = new Message("{\"action\": \"button_click\", " +
            "\"page\": \"book_card\", \"msisdn\": \"12345\"}",
            Message.EnrichmentType.MSISDN);
    public static final Message CORRECT_VASYA_ENRICHED = new Message("{\"action\": \"button_click\", " +
            "\"page\": \"book_card\", \"msisdn\": \"12345\"}",
            Message.EnrichmentType.MSISDN);

    public static final String NOT_FOUND_PHONE = "1";
    public static final String VASYA_PHONE = "12345";
    public static final String KOLYA_PHONE = "8913";
    public static final String PETYA_PHONE = "138500";
    public static User getVasya() {
        return new User("Vasya", "Petrov", VASYA_PHONE);
    }


    public static User getKolya() {
        return new User("Kolya", "Ivanov", KOLYA_PHONE);
    }

    public static User getNikolay() {
        return new User("Nikolay", "Ivanov", KOLYA_PHONE);
    }

    public static User getPetya() {
        return new User("petya", "fishman", PETYA_PHONE);
    }

    public static Map<String, String> getNewStandardVasyaContentMap() {
        HashMap<String, String> content = new HashMap<>();
        content.put("action", "button_click");
        content.put("page", "book_card");
        content.put("msisdn", "12345");
        return content;
    }

    public static Map<String, String> getNewStandardEnrichedVasyaContentMap() {
        HashMap<String, String> content = new HashMap<>();
        content.put("action", "button_click");
        content.put("page", "book_card");
        content.put("msisdn", "12345");
        HashMap<String, String> enrichment = new HashMap<>();
        User vasya = getVasya();
        enrichment.put("firstname", vasya.getFirstname());
        enrichment.put("lastname", vasya.getLastname());
        content.put("enrichment", enrichment.toString());
        return content;
    }

    public static UserStorage getStandardUserStorage() {
        UserStorage userStorage = new UserStorageInMemory(new ConcurrentHashMap<>());
        userStorage.add(getVasya());
        userStorage.add(getKolya());
        return userStorage;
    }
}
